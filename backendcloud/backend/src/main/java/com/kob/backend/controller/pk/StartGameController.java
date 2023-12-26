package com.kob.backend.controller.pk;

import com.kob.backend.service.pk.StartGameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequestMapping("/pk")
public class StartGameController {
    private StartGameService startGameService;

    @Autowired
    public StartGameController(StartGameService startGameService) {
        this.startGameService = startGameService;
    }

    @PostMapping("/startGame/")
    public String startGame(@RequestParam MultiValueMap<String, String> gameData) {
        Integer aId = Integer.parseInt(Objects.requireNonNull(gameData.getFirst("aId")));
        Integer aBotId = Integer.parseInt(Objects.requireNonNull(gameData.getFirst("aBotId")));
        Integer bId = Integer.parseInt(Objects.requireNonNull(gameData.getFirst("bId")));
        Integer bBotId = Integer.parseInt(Objects.requireNonNull(gameData.getFirst("bBotId")));

        return startGameService.startGame(aId, aBotId, bId, bBotId);
    }
}
