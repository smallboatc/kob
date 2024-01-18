package com.kob.backend.controller.user.account.qq;

import com.alibaba.fastjson.JSONObject;
import com.kob.backend.service.user.account.qq.WebService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/user/account/qq/web")
public class WebServiceController {
    private WebService webService;

    @Autowired
    public WebServiceController(WebService webService) {
        this.webService = webService;
    }

    @GetMapping("/applyCode/")
    public JSONObject applyCode() {
        return webService.applyCode();
    }

    @GetMapping("/receiveCode/")
    public JSONObject receiveCode(@RequestParam Map<String, String> data) {
        String code = data.get("code");
        String state = data.get("state");
        return webService.receiveCode(code, state);
    }
}
