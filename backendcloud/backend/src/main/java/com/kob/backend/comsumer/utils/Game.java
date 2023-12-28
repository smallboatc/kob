package com.kob.backend.comsumer.utils;

import com.alibaba.fastjson.JSONObject;
import com.kob.backend.comsumer.WebSocketServer;
import com.kob.backend.pojo.Bot;
import com.kob.backend.pojo.Record;
import com.kob.backend.pojo.User;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

public class Game extends Thread{
    /**
     * 游戏地图行数
     */
    private final Integer rows;
    /**
     * 游戏地图列数
     */
    private final Integer cols;
    /**
     * 地图内部墙体障碍物数量
     */
    private final Integer innerWallsCount;
    /**
     * 游戏地图（0 表示草地，1 表示墙体障碍物）
     */
    private final int[][] g;

    private final static int[] dx = {-1, 0, 1, 0}, dy = {0, 1, 0, -1};

    private Player playerA, playerB;
    private Integer nextStepA = null;
    private Integer nextStepB = null;
    private ReentrantLock lock = new ReentrantLock();
    /**
     * 游戏状态，playing 表示游戏进行中， finished 表示游戏结束
     */
    private String status = "playing";

    /**
     * 赢家，all 表示平局
     */
    private String winner = "";

    private final static String ADD_BOT_URL = "http://127.0.0.1:3002/bot/add/";

    public Game(Integer rows, Integer cols, Integer innerWallsCount, Integer idA, Bot botA, Integer idB, Bot botB) {
        this.rows = rows;
        this.cols = cols;
        this.innerWallsCount = innerWallsCount;
        this.g = new int[rows][cols];

        Integer botIdA = -1, botIdB = -1;
        String botCodeA = "", botCodeB = "";
        if (null != botA) {
            botIdA = botA.getId();
            botCodeA = botA.getContent();
        }
        if (null != botB) {
            botIdB = botB.getId();
            botCodeB = botB.getContent();
        }
        playerA = new Player(idA, botIdA, botCodeA, rows - 2, 1, new ArrayList<>());
        playerB = new Player(idB, botIdB, botCodeB, 1, cols - 2, new ArrayList<>());
    }

    public Player getPlayerA() {
        return playerA;
    }

    public Player getPlayerB() {
        return playerB;
    }

    public void setNextStepA(Integer nextStepA) {
        lock.lock();
        try {
            this.nextStepA = nextStepA;
        } finally {
            lock.unlock();
        }
    }
    public void setNextStepB(Integer nextStepB) {
        lock.lock();
        try {
            this.nextStepB = nextStepB;
        } finally {
            lock.unlock();
        }
    }

    public int[][] getG() {
        return g;
    }

    // Flood Fill 校验地图连通性
    public boolean checkConnectivity(int sx, int sy, int tx, int ty) {
        if (sx == tx && sy == ty) return true;
        // 表示已经走过
        g[sx][sy] = 1;
        for (int d = 0; d < 4; d ++) {
            int a = sx + dx[d], b = sy + dy[d];
            if (a >= 0 && a < this.rows && b >= 0 && b < this.cols && g[a][b] == 0) {
                if (checkConnectivity(a, b, tx, ty)) {
                    // 恢复现场
                    g[sx][sy] = 0;
                    return true;
                }
            }
        }
        // 即使校验失败也要恢复现场
        g[sx][sy] = 0;
        return false;

    }

    // 画地图
    private boolean draw() {
        // 初始化地图
        for (int i = 0; i < g.length; i ++) {
            Arrays.fill(g[i], 0);
        }
        // 生成四周墙体障碍物
        for (int r = 0; r < this.rows; r ++) {
            g[r][0] = g[r][this.cols - 1] = 1;
        }
        for (int c = 0; c < this.cols; c ++) {
            g[0][c] = g[this.rows - 1][c] = 1;
        }
        // 生成地图内部随机墙体障碍物
        Random random = new Random();
        for (int i = 0; i < this.innerWallsCount >> 1; i ++) {
            for (int j = 0; j < 1000; j ++) {
                int r = random.nextInt(this.rows);
                int c = random.nextInt(this.cols);
                if (g[r][c] == 1 || g[this.rows - 1 - r][this.cols - 1 - c] == 1) {
                    continue;
                }
                if (r == this.rows - 2 && c == 1 || r == 1 && c == this.cols - 2) {
                    continue;
                }
                g[r][c] = g[this.rows - 1 - r][this.cols - 1 - c] = 1;
                break;
            }
        }
        // 校验连通性
        return checkConnectivity(this.rows - 2, 1, 1, this.cols - 2);
    }

    public void createMap() {
        // 循环 1000 次，直到成功生成合法地图
        for (int i = 0; i < 1000; i ++) {
            if (draw()) {
                break;
            }
        }
    }

    private String getInput(Player player) {
        Player me, you;
        if (playerA.getId().equals(player.getId())) {
            me = playerA;
            you = playerB;
        } else {
            me = playerB;
            you = playerA;
        }

        return getMapString() + "#" +
                me.getSx() + "#" +
                me.getSy() + "#(" +
                me.getStepsString() + ")#" +
                you.getSx() + "#" +
                you.getSy() + "#(" +
                you.getStepsString() + ")";
    }

    private void sendBotCode(Player player) {
        // 先判断是否是人工出战
        if (player.getBotId().equals(-1)) return;
        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("userId", player.getId().toString());
        data.add("botCode", player.getBotCode());
        data.add("input", getInput(player));

        WebSocketServer.restTemplate.postForObject(ADD_BOT_URL, data, String.class);
    }

    /**
     * 等待两名玩家的下一步操作
     * @return
     */
    private boolean nextStep() {
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        sendBotCode(playerA);
        sendBotCode(playerB);

        for (int i = 0; i < 50; i ++) {
            try {
                Thread.sleep(100);
                lock.lock();
                try {
                    if (null != nextStepA && null != nextStepB) {
                        playerA.getSteps().add(nextStepA);
                        playerB.getSteps().add(nextStepB);
                        return true;
                    }
                } finally {
                    lock.unlock();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 校验 cellsA的最后一步是否合法，需要校验 B 只需要将传递的参数换位即可
     * @param cellsA
     * @param cellsB
     * @return
     */
    private boolean checkValid(List<Cell> cellsA, List<Cell> cellsB) {
        int n = cellsA.size();
        Cell cell = cellsA.get(n - 1);
        // 如果撞墙，当前蛇直接寄
        if (g[cell.getX()][cell.getY()] == 1) return false;
        // 校验最后一步操作是否会与自己或者对手的身体相撞
        for (int i = 0; i < n - 1; i ++) {
            if (cellsA.get(i).getX() == cell.getX() && cellsA.get(i).getY() == cell.getY()
                    || cellsB.get(i).getX() == cell.getX() && cellsB.get(i).getY() == cell.getY()) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断两名玩家下一步操作是否合法
     */
    private void judge() {
        List<Cell> cellsA = playerA.getCells();
        List<Cell> cellsB = playerB.getCells();
        boolean validA = checkValid(cellsA, cellsB);
        boolean validB = checkValid(cellsB, cellsA);

        if (!validA || !validB) {
            status = "finished";
            if (!validA && !validB) {
                winner = "all";
            } else if (!validA) {
                winner = "B";
            } else {
                winner = "A";
            }
        }
    }

    /**
     * 广播消息通用方法
     * @param message
     */
    private void sendAllMessage(String message) {
        if (null != WebSocketServer.users.get(playerA.getId())) {
            WebSocketServer.users.get(playerA.getId()).sendMessage(message);
        }
        if (null != WebSocketServer.users.get(playerB.getId())) {
            WebSocketServer.users.get(playerB.getId()).sendMessage(message);
        }
    }

    /**
     * 向两名玩家广播两名玩家的操作
     */
    private void sendMove() {
        lock.lock();
        try {
            JSONObject resp = new JSONObject();
            resp.put("event", "move");
            resp.put("a_direction", nextStepA);
            resp.put("b_direction", nextStepB);
            sendAllMessage(resp.toJSONString());
            nextStepA = nextStepB = null;
        } finally {
            lock.unlock();
        }
    }

    private String getMapString() {
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < rows; i ++) {
            for (int j = 0; j < cols; j ++) {
                res.append(g[i][j]);
            }
        }
        return res.toString();
    }

    private void updateUserRating(Player player, Integer rating) {
        User user = WebSocketServer.userMapper.selectById(player.getId());
        user.setRating(rating);
        WebSocketServer.userMapper.updateById(user);
    }

    /**
     * 将当前对局信息存入数据库
     */
    private void saveToDatabase() {
        Integer ratingA = WebSocketServer.userMapper.selectById(playerA.getId()).getRating();
        Integer ratingB = WebSocketServer.userMapper.selectById(playerB.getId()).getRating();

        if ("A".equals(winner)) {
            ratingA += 5;
            ratingB -= 3;
        } else if ("B".equals(winner)) {
            ratingB += 5;
            ratingA -= 3;
        }

        updateUserRating(playerA, ratingA);
        updateUserRating(playerB, ratingB);

        Record record = new Record(
                null,
                playerA.getId(),
                playerA.getSx(),
                playerA.getSy(),
                playerA.getStepsString(),
                playerB.getId(),
                playerB.getSx(),
                playerB.getSy(),
                playerB.getStepsString(),
                getMapString(),
                winner,
                null
        );
        WebSocketServer.recordMapper.insert(record);
    }

    /**
     * 向两名玩家广播游戏结果
     */
    private void sendResult() {
        JSONObject resp = new JSONObject();
        resp.put("event", "result");
        resp.put("winner", winner);
        saveToDatabase();
        sendAllMessage(resp.toJSONString());
    }

    @Override
    public void run() {
        for (int i = 0; i < 1000; i ++) {
            if (nextStep()) {
                judge();
                if ("playing".equals(status)) {
                    sendMove();
                } else {
                    sendResult();
                    break;
                }
            } else {
                status = "finished";
                lock.lock();
                try {
                    if (null == nextStepA && null == nextStepB) {
                        winner = "all";
                    } else if (null == nextStepB) {
                        winner = "A";
                    } else {
                        winner = "B";
                    }
                } finally {
                    lock.unlock();
                }
                sendResult();
                break;
            }
        }
    }
}
