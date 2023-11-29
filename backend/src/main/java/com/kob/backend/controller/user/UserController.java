package com.kob.backend.controller.user;

import com.kob.backend.mapper.UserMapper;
import com.kob.backend.pojo.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserMapper userMapper;

    @Autowired
    public UserController(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @GetMapping("/getAll")
    public List<User> getAll() {
        return userMapper.selectList(null);
    }

    @GetMapping("/addUser")
    public String addUser(@Param("username") String username, @Param("password") String password) {
        // 加密密码
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        password = passwordEncoder.encode(password);
        User user = new User(1, username, password);
        int state = userMapper.insert(user);
        return state > 0 ? "SUCCESS" : "FAIL";
    }

}
