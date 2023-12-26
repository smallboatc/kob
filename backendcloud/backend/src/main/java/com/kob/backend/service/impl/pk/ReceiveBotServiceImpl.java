package com.kob.backend.service.impl.pk;

import com.kob.backend.comsumer.WebSocketServer;
import com.kob.backend.comsumer.utils.Game;
import com.kob.backend.service.pk.ReceiveBotMoveService;
import org.springframework.stereotype.Service;

@Service
public class ReceiveBotServiceImpl implements ReceiveBotMoveService {
    @Override
    public String receiveBotMove(Integer userId, Integer direction) {
        System.out.println("receive bot move: " + userId + " " + direction);
        if (null != WebSocketServer.users.get(userId)) {
            Game game = WebSocketServer.users.get(userId).game;
            if (null != game) {
                if (game.getPlayerA().getId().equals(userId)) {
                    game.setNextStepA(direction);
                } else if (game.getPlayerB().getId().equals(userId)) {
                    game.setNextStepB(direction);
                }
            } else {
                System.out.println("Game为空！！！");
            }
        }
        return "receiveBotMove success";
    }
}
