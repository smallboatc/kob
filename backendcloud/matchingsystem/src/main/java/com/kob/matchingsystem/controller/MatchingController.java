package com.kob.matchingsystem.controller;

import com.kob.matchingsystem.service.MatchingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequestMapping("/player")
public class MatchingController {
    private MatchingService matchingService;

    @Autowired
    public MatchingController(MatchingService matchingService) {
        this.matchingService = matchingService;
    }

    @PostMapping("/add/")
    public String addPlayer(@RequestParam MultiValueMap<String, String> playerData) {
        Integer userId = Integer.parseInt(Objects.requireNonNull(playerData.getFirst("userId")));
        Integer rating = Integer.parseInt(Objects.requireNonNull(playerData.getFirst("rating")));
        Integer botId = Integer.parseInt(Objects.requireNonNull(playerData.getFirst("botId")));
        return matchingService.addPlayer(userId, rating, botId);
    }

    @PostMapping("/remove")
    public String removePlayer(@RequestParam MultiValueMap<String, String> playerData) {
        Integer userId = Integer.parseInt(Objects.requireNonNull(playerData.getFirst("userId")));
        return matchingService.removePlayer(userId);
    }

}
