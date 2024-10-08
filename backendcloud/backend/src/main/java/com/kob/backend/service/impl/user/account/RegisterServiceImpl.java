package com.kob.backend.service.impl.user.account;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kob.backend.mapper.UserMapper;
import com.kob.backend.pojo.User;
import com.kob.backend.service.user.account.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class RegisterServiceImpl implements RegisterService {
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    /**
     * 每个用户创建时初始天梯分默认为 1500
     */
    private final Integer DEFAULT_RATING = 1500;

    private final Random random = new Random();

    @Autowired
    public RegisterServiceImpl(UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Map<String, String> register(String username, String password, String confirmedPassword) {
        Map<String, String> map = new HashMap<>();
        if (null == username) {
            map.put("error_message", "用户名不能为空！");
            return map;
        }

        // 判断是否包含特殊字符
        String regEx = "[ _`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]|\n|\r|\t";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(username);
        if (m.find()) {
            map.put("error_message", "用户名不能包含特殊字符！");
            return map;
        }

        if (username.length() > 30) {
            map.put("error_message", "用户名过长！");
            return map;
        }

        if (null == password || null == confirmedPassword || password.length() == 0 || confirmedPassword.length() == 0) {
            map.put("error_message", "密码不能为空！");
            return map;
        }

        if (!password.equals(confirmedPassword)) {
            map.put("error_message", "两次输入的密码不一致！");
            return map;
        }

        if (password.length() < 6) {
            map.put("error_message", "密码长度必须大于等于6！");
            return map;
        }

        if (password.length() > 50) {
            map.put("error_message", "密码过长！");
            return map;
        }

        // 校验用户名是否已被使用
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        if (userMapper.selectCount(queryWrapper) > 0) {
            map.put("error_message", "用户名已存在，换一个吧！");
            return map;
        }

        // 当以上校验均通过时，将该用户存入数据库中
        String encodePassword = passwordEncoder.encode(password);
        String[] photos = {
                "https://cdn.acwing.com/media/article/image/2024/03/29/126318_b71a3d44ed-luffy.png",
                "https://cdn.acwing.com/media/article/image/2024/01/21/126318_56105ae0b8-霸气熊猫.png",
                "https://cdn.acwing.com/media/article/image/2024/01/21/126318_599fb5d4b8-小犀牛.png",
                "https://cdn.acwing.com/media/article/image/2024/01/21/126318_5d2d0c56b8-一二.png"
        };
        int idx = random.nextInt(4);
        String photo = photos[idx];
        User user = new User(null, username, encodePassword, photo, DEFAULT_RATING, null, null, null);
        userMapper.insert(user);

        // 正常流程结束
        map.put("error_message", "success");

        return map;
    }
}
