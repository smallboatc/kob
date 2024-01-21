package com.kob.matchingsystem.service.impl.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class MatchingPool extends Thread{
    private static List<Player> players = new ArrayList<>();
    private final ReentrantLock lock = new ReentrantLock();

    private final static String START_GAME_URL = "http://127.0.0.1:3000/pk/startGame/";

    private static RestTemplate restTemplate;

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        MatchingPool.restTemplate = restTemplate;
    }

    public void addPlayer(Integer userId, Integer rating, Integer botId) {
        lock.lock();
        try {
            players.add(new Player(userId, botId, rating, 0));
        } finally {
            lock.unlock();
        }
    }

    public void removePlayer(Integer userId) {
        lock.lock();
        try {
            List<Player> newPlayers = new ArrayList<>();
            for (Player player : players) {
                if (!player.getUserId().equals(userId)) {
                    newPlayers.add(player);
                }
            }
            players = newPlayers;
        } finally {
            lock.unlock();
        }
    }

    // 将所有当前等待玩家的等待时间 + 1
    private void increaseWaitedTime() {
        for (Player player : players) {
            player.setWaitedTime(player.getWaitedTime() + 1);
        }
    }

    // 判断两名玩家是否匹配
    private boolean checkMatchable(Player a, Player b) {
        int ratingDelta = Math.abs(a.getRating() - b.getRating());
        int waitedTime = Math.min(a.getWaitedTime(), b.getWaitedTime());
        return ratingDelta <= waitedTime * 10 && a.getUserId() != b.getUserId();
    }

    // 返回匹配成功结果
    private void sendResult(Player a, Player b) {
        // System.out.println("matched: " + a + " " + b);
        MultiValueMap<String, String> gameData = new LinkedMultiValueMap<>();
        gameData.add("aId", a.getUserId().toString());
        gameData.add("aBotId", a.getBotId().toString());
        gameData.add("bId", b.getUserId().toString());
        gameData.add("bBotId", b.getBotId().toString());
        restTemplate.postForObject(START_GAME_URL, gameData, String.class);
    }

    // 尝试匹配所有玩家
    private void matchPlayers() {
        // System.out.println("matching... " + players.toString());
        boolean[] used = new boolean[players.size()];
        // 从前往后遍历，会优先匹配等待更久的玩家
        for (int i = 0; i < players.size(); i ++) {
            if (used[i]) continue;
            Player a = players.get(i);
            // 等待时间超过到达15秒直接匹配人机
            if (players.get(i).getWaitedTime() >= 15) {
                used[i] = true;
                // 生成一个人机
                Player b = new Player(-1, -1, 1500, 0);
                sendResult(a, b);
                continue;
            }
            for (int j = i + 1; j < players.size(); j ++) {
                if (used[j]) continue;
                Player b = players.get(j);
                if (checkMatchable(a, b)) {
                    used[i] = used[j] = true;
                    sendResult(a, b);
                    break;
                }
            }
        }

        List<Player> newPlayers = new ArrayList<>();
        for (int i = 0; i < players.size(); i ++) {
            if (!used[i]) {
                newPlayers.add(players.get(i));
            }
        }
        players = newPlayers;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(1000);
                lock.lock();
                try {
                    increaseWaitedTime();
                    matchPlayers();
                } finally {
                    lock.unlock();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
