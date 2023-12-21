package com.kob.backend.service.user.account;

import java.util.Map;

public interface LoginService {
    /**
     * 校验登录，并返回 token
     * @param username
     * @param password
     * @return
     */
    Map<String, String> getToken(String username, String password);
}
