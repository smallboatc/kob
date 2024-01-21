package com.kob.botrunningsystem.controller;

import com.kob.botrunningsystem.service.BotRunningService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequestMapping("/bot")
public class BotRunningController {
    private final BotRunningService botRunningService;

    @Autowired
    public BotRunningController(BotRunningService botRunningService) {
        this.botRunningService = botRunningService;
    }

    @PostMapping("/add/")
    public String addBot(@RequestParam MultiValueMap<String, String> botData) {
        Integer userId = Integer.parseInt(Objects.requireNonNull(botData.getFirst("userId")));
        String botCode = botData.getFirst("botCode");
        String input = botData.getFirst("input");
        return botRunningService.addBot(userId, botCode, input);
    }
}
