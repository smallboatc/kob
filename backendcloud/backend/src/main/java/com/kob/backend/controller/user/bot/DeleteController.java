package com.kob.backend.controller.user.bot;

import com.kob.backend.service.user.bot.DeleteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/user/bot")
public class DeleteController {
    private final DeleteService deleteService;

    @Autowired
    public DeleteController(DeleteService deleteService) {
        this.deleteService = deleteService;
    }

    @PostMapping("/delete")
    public Map<String, String> delete(@RequestParam Map<String, String> botData) {
        return deleteService.delete(botData);
    }
}
