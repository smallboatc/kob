package com.kob.backend.service.impl.user.account.acwing;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kob.backend.mapper.UserMapper;
import com.kob.backend.pojo.User;
import com.kob.backend.service.impl.user.account.utils.HttpClientUtil;
import com.kob.backend.service.user.account.acwing.AcAppService;
import com.kob.backend.utils.JwtUtil;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.Duration;
import java.util.*;

@Service
public class AcAppServiceImpl implements AcAppService {
    private static final String APPID = "5163";
    private static final String APP_SECRET = "bf9e6965752f47ef93c6969264390f10";
    // 回调链接
    private static final String REDIRECT_URI = "https://app5163.acapp.acwing.com.cn/api/user/account/acwing/acapp/receiveCode/";
    // 申请授权令牌 url
    private static final String APPLY_ACCESS_TOKEN_URL = "https://www.acwing.com/third_party/api/oauth2/access_token/";
    // 申请用户信息 url
    private static final String APPLY_USER_INFO_URL = "https://www.acwing.com/third_party/api/meta/identity/getinfo/";
    private static final Random random = new Random();
    private final RedisTemplate<String, String> redisTemplate;
    private final UserMapper userMapper;

    @Autowired
    public AcAppServiceImpl(RedisTemplate<String, String> redisTemplate, UserMapper userMapper) {
        this.redisTemplate = redisTemplate;
        this.userMapper = userMapper;
    }

    @Override
    public JSONObject applyCode() {
        JSONObject resp = new JSONObject();
        resp.put("appid", APPID);
        try {
            resp.put("redirect_uri", URLEncoder.encode(REDIRECT_URI, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            resp.put("result", "failed");
            return resp;
        }
        resp.put("scope", "userinfo");

        StringBuilder state = new StringBuilder();
        for (int i = 0; i < 10; i ++) {
            state.append((char) (random.nextInt(10) + '0'));
        }
        resp.put("state", state.toString());
        redisTemplate.opsForValue().set(state.toString(), "true");
        // 设置10分钟过期
        redisTemplate.expire(state.toString(), Duration.ofMinutes(10));
        resp.put("result", "success");
        return resp;
    }

    @Override
    public JSONObject receiveCode(String code, String state) {
        JSONObject resp = new JSONObject();
        resp.put("result", "failed");
        if (null == code || null == state) return resp;
        if (Boolean.FALSE.equals(redisTemplate.hasKey(state))) return resp;
        redisTemplate.delete(state);
        List<NameValuePair> nameValuePairs = new LinkedList<>();
        nameValuePairs.add(new BasicNameValuePair("appid", APPID));
        nameValuePairs.add(new BasicNameValuePair("secret", APP_SECRET));
        nameValuePairs.add(new BasicNameValuePair("code", code));

        String getString = HttpClientUtil.get(APPLY_ACCESS_TOKEN_URL, nameValuePairs);
        if (null == getString) return resp;
        JSONObject getResp = JSONObject.parseObject(getString);
        String accessToken = getResp.getString("access_token");
        String openid = getResp.getString("openid");
        if (null == accessToken || null == openid) return resp;

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("openid", openid);
        List<User> users = userMapper.selectList(queryWrapper);
        // 当用户已存在
        if (null != users && users.size() != 0) {
            User user = users.get(0);
            String jwt = JwtUtil.createJWT(user.getId().toString());
            resp.put("result", "success");
            resp.put("jwt", jwt);
            return resp;
        }
        // 当用户第一次使用三方登录
        nameValuePairs = new LinkedList<>();
        nameValuePairs.add(new BasicNameValuePair("access_token", accessToken));
        nameValuePairs.add(new BasicNameValuePair("openid", openid));
        getString = HttpClientUtil.get(APPLY_USER_INFO_URL, nameValuePairs);
        if (null == getString) return resp;
        getResp = JSONObject.parseObject(getString);
        String username = getResp.getString("username");
        String photo = getResp.getString("photo");

        if (null == username || null == photo) return resp;
        // 为了使得每个用户名不重复，每次循环重复概率的概率会是上一次的 1/10
        for (int i = 0; i < 100; i ++) {
            QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
            userQueryWrapper.eq("username", username);
            if (userMapper.selectCount(userQueryWrapper) == 0) break;
            username += (char)(random.nextInt(10) + '0');
            if (i == 99) return resp;
        }

        User user = new User(null, username, null, photo, 1500, openid, null, null);
        userMapper.insert(user);
        String jwt = JwtUtil.createJWT(user.getId().toString());
        resp.put("result", "success");
        resp.put("jwt", jwt);
        return resp;
    }
}
