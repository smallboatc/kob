package com.kob.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
class BackendApplicationTests {

    /**
     * 测试用户输入密码是否与数据库中存储的加密后密码相匹配
     */
    @Test
    void contextLoads() {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        System.out.println(passwordEncoder.encode("cxc"));
        System.out.println(passwordEncoder.encode("cxc"));
        System.out.println(passwordEncoder.encode("123456"));
        // true
        System.out.println(passwordEncoder.matches("cxc",
                "$2a$10$cKvVr/6q/zHnOqP0qeIfoeae2wZNtJRmgu6hxomCTfUhPXuMr.8fy"));
    }
}
