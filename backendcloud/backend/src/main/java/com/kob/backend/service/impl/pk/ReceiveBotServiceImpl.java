package com.kob.backend.service.impl.pk;

import com.kob.backend.comsumer.WebSocketServer;
import com.kob.backend.comsumer.utils.Game;
import com.kob.backend.service.pk.ReceiveBotMoveService;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class ReceiveBotServiceImpl implements ReceiveBotMoveService {
    @Override
    public String receiveBotMove(Integer userId, Integer direction) {
         // System.out.println("receive bot move: " + userId + " " + direction);
        // 判断人机
        if (userId < 0) {
            WebSocketServer.redisTemplate.opsForValue().set(userId.toString(), direction.toString());
            // 设置 5秒过期
            WebSocketServer.redisTemplate.expire(userId.toString(), Duration.ofMillis(5000));
        } else if (null != WebSocketServer.users.get(userId)) {
            Game game = WebSocketServer.users.get(userId).game;
            if (null != game) {
                if (game.getPlayerA().getId().equals(userId)) {
                    game.setNextStepA(direction);
                } else if (game.getPlayerB().getId().equals(userId)) {
                    game.setNextStepB(direction);
                }
            }
        }
        return "receiveBotMove success";
    }
}
