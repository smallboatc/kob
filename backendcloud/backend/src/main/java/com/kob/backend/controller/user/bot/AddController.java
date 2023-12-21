package com.kob.backend.controller.user.bot;

import com.kob.backend.service.user.bot.AddService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/user/bot/")
public class AddController {
    private final AddService addService;

    @Autowired
    public AddController(AddService addService) {
        this.addService = addService;
    }

    @PostMapping("add")
    public Map<String, String> add(@RequestParam Map<String, String> botData) {
        return addService.add(botData);
    }
}
