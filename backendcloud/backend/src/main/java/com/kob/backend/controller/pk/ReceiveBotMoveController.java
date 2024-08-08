package com.kob.backend.controller.pk;

import com.kob.backend.service.pk.ReceiveBotMoveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequestMapping("/pk")
public class ReceiveBotMoveController {
    private final ReceiveBotMoveService receiveBotMoveService;

    @Autowired
    public ReceiveBotMoveController(ReceiveBotMoveService receiveBotMoveService) {
        this.receiveBotMoveService = receiveBotMoveService;
    }


    @PostMapping("/receiveBotMove/")
    public String receiveBotService(@RequestParam MultiValueMap<String, String> data) {
        Integer userId = Integer.parseInt(Objects.requireNonNull(data.getFirst("userId")));
        Integer direction = Integer.parseInt(Objects.requireNonNull(data.getFirst("direction")));

        return receiveBotMoveService.receiveBotMove(userId, direction);
    }
}
