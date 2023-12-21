package com.kob.backend.service.user.account;

import java.util.Map;

public interface RegisterService {
    /**
     * 用户注册
     * @param username
     * @param password
     * @param confirmedPassword
     * @return
     */
    Map<String, String> register(String username, String password, String confirmedPassword);
}
