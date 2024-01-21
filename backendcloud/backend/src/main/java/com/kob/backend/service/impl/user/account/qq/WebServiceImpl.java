package com.kob.backend.service.impl.user.account.qq;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kob.backend.mapper.UserMapper;
import com.kob.backend.pojo.User;
import com.kob.backend.service.impl.user.account.utils.HttpClientUtil;
import com.kob.backend.service.user.account.qq.WebService;
import com.kob.backend.utils.JwtUtil;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.Duration;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

@Service
public class WebServiceImpl implements WebService {
    private static final String APP_ID = "102091416";
    private static final String APP_SECRET = "OtKizCUisshIeox3";
    private static final String REDIRECT_URI = "https://app5163.acapp.acwing.com.cn/user/account/qq/web/receiveCode";
    private static final String APPLY_ACCESS_TOKEN_URL = "https://graph.qq.com/oauth2.0/token";
    private static final String APPLY_USER_INFO_URL = "https://graph.qq.com/user/get_user_info";
    private static final String APPLY_USER_OPENID_URL="https://graph.qq.com/oauth2.0/me";
    private static final Random random = new Random();
    private final UserMapper userMapper;
    private final RedisTemplate<String, String> redisTemplate;

    @Autowired
    public WebServiceImpl(UserMapper userMapper, RedisTemplate<String, String> redisTemplate) {
        this.userMapper = userMapper;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public JSONObject applyCode() {
        JSONObject resp = new JSONObject();
        String encodeUrl;
        try {
            encodeUrl = URLEncoder.encode(REDIRECT_URI, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            resp.put("result", "failed");
            return resp;
        }

        // 随机字符串，防止 csrf 攻击
        StringBuilder state = new StringBuilder();
        for (int i = 0; i < 10; i ++) {
            state.append((char)(random.nextInt(10) + '0'));
        }
        // 存到redis里，有效期设置为10分钟
        resp.put("result", "success");
        redisTemplate.opsForValue().set(state.toString(), "true");
        redisTemplate.expire(state.toString(), Duration.ofMinutes(10));

        String applyCodeUrl = "https://graph.qq.com/oauth2.0/authorize"
                + "?response_type="+"code"
                + "&client_id=" + APP_ID
                + "&redirect_uri=" + encodeUrl
                + "&state=" + state;
        resp.put("apply_code_url", applyCodeUrl);

        return resp;
    }

    @Override
    public JSONObject receiveCode(String code, String state) {
        JSONObject resp = new JSONObject();
        resp.put("result", "failed");
        if (null == code || null == state) return resp;
        if (Boolean.FALSE.equals(redisTemplate.hasKey(state))) return resp;
        redisTemplate.delete(state);
        // 获取access_token
        List<NameValuePair> nameValuePairs = new LinkedList<>();
        nameValuePairs.add(new BasicNameValuePair("grant_type", "authorization_code"));
        nameValuePairs.add(new BasicNameValuePair("client_id", APP_ID));
        nameValuePairs.add(new BasicNameValuePair("client_secret", APP_SECRET));
        nameValuePairs.add(new BasicNameValuePair("code", code));
        nameValuePairs.add(new BasicNameValuePair("redirect_uri", REDIRECT_URI));
        nameValuePairs.add(new BasicNameValuePair("fmt", "json"));

        String getString = HttpClientUtil.get(APPLY_ACCESS_TOKEN_URL, nameValuePairs);
        if (null == getString) return resp;
        JSONObject getResp = JSONObject.parseObject(getString);
        String accessToken = getResp.getString("access_token");

        // 获取openid
        nameValuePairs = new LinkedList<>();
        nameValuePairs.add(new BasicNameValuePair("access_token", accessToken));
        nameValuePairs.add(new BasicNameValuePair("fmt", "json"));

        getString = HttpClientUtil.get(APPLY_USER_OPENID_URL, nameValuePairs);
        if(null == getString) return resp;
        getResp = JSONObject.parseObject(getString);
        String openid = getResp.getString("openid");

        if (accessToken == null || openid == null) return resp;

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("openid_qq", openid);
        List<User> users = userMapper.selectList(queryWrapper);

        // 用户已经授权，自动登录
        if (null != users && !users.isEmpty()) {
            User user = users.get(0);
            // 生成jwt
            String jwt = JwtUtil.createJWT(user.getId().toString());

            resp.put("result", "success");
            resp.put("jwt", jwt);
            return resp;
        }

        // 新用户授权，获取用户信息，并创建新用户
        nameValuePairs = new LinkedList<>();
        nameValuePairs.add(new BasicNameValuePair("access_token", accessToken));
        nameValuePairs.add(new BasicNameValuePair("openid", openid));
        nameValuePairs.add(new BasicNameValuePair("oauth_consumer_key", APP_ID));
        getString = HttpClientUtil.get(APPLY_USER_INFO_URL, nameValuePairs);
        if (null == getString) return resp;

        getResp = JSONObject.parseObject(getString);
        String username = getResp.getString("nickname");
        // 50*50的头像
        String photo = getResp.getString("figureurl_1");

        if (null == username || null == photo) return resp;

        // 每次循环，用户名重复的概率为上一次的1/10
        for (int i = 0; i < 100; i ++) {
            QueryWrapper<User> usernameQueryWrapper = new QueryWrapper<>();
            usernameQueryWrapper.eq("username", username);
            if (userMapper.selectCount(usernameQueryWrapper) > 0) break;
            username += (char)(random.nextInt(10) + '0');
            if (i == 99) return resp;
        }
        User user = new User(null, username, null, photo, 1500, null, openid, null);
        userMapper.insert(user);
        // 生成 jwt
        String jwt = JwtUtil.createJWT(user.getId().toString());
        resp.put("result", "success");
        resp.put("jwt", jwt);
        return resp;
    }
}
