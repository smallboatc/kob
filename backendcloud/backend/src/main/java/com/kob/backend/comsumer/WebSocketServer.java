package com.kob.backend.comsumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.kob.backend.comsumer.utils.Game;
import com.kob.backend.comsumer.utils.JwtAuthentication;
import com.kob.backend.mapper.RecordMapper;
import com.kob.backend.mapper.UserMapper;
import com.kob.backend.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
@ServerEndpoint("/websocket/{token}")  // 不要以'/'结尾
public class WebSocketServer {

    private Session session;
    private User user;
    // 记录全局的连接信息
    public static final ConcurrentHashMap<Integer, WebSocketServer> users = new ConcurrentHashMap<>();

    private Game game = null;

    private final static String ADD_PLAYER_URL = "http://127.0.0.1:8089/player/add/";

    private final static String REMOVE_PLAYER_URL = "http://127.0.0.1:8089/player/remove/";

    private static UserMapper userMapper;

    public static RecordMapper recordMapper;

    private static RestTemplate restTemplate;

    @Autowired
    public void setUserMapper(UserMapper userMapper) {
        WebSocketServer.userMapper = userMapper;
    }

    @Autowired
    public void setRecordMapper(RecordMapper recordMapper) {
        WebSocketServer.recordMapper = recordMapper;
    }

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        WebSocketServer.restTemplate = restTemplate;
    }


    @OnOpen
    public void onOpen(Session session, @PathParam("token") String token) throws IOException {
        // 建立连接
        this.session = session;
        Integer userId = JwtAuthentication.getUserId(token);
        this.user = userMapper.selectById(userId);

        if (null != this.user) {
            users.put(userId, this);
            System.out.println("connected!");
        } else {
            this.session.close();
        }

        System.out.println(users);

    }

    @OnClose
    public void onClose() {
        // 关闭链接
        System.out.println("disconnected!");
        if (null != this.user) {
            users.remove(this.user.getId());
        }
    }

    public static void startGame(Integer aId, Integer bId) {
        User a = userMapper.selectById(aId);
        User b = userMapper.selectById(bId);

        Game game = new Game(13, 14, 20, a.getId(), b.getId());
        game.createMap();
        game.start();
        if (null != users.get(a.getId())) {
            users.get(a.getId()).game = game;
        }
        if (null != users.get(b.getId())) {
            users.get(b.getId()).game = game;
        }

        JSONObject respGame = new JSONObject();
        respGame.put("a_id", game.getPlayerA().getId());
        respGame.put("a_sx", game.getPlayerA().getSx());
        respGame.put("a_sy", game.getPlayerA().getSy());
        respGame.put("b_id", game.getPlayerB().getId());
        respGame.put("b_sx", game.getPlayerB().getSx());
        respGame.put("b_sy", game.getPlayerB().getSy());
        respGame.put("map", game.getG());

        JSONObject respA = new JSONObject();
        respA.put("event", "start-matching");
        respA.put("opponent_username", b.getUsername());
        respA.put("opponent_photo", b.getPhoto());
        respA.put("game", respGame);
        // 给玩家A客户端返回结果
        if (null != users.get(a.getId())) {
            users.get(a.getId()).sendMessage(respA.toJSONString());
        }

        JSONObject respB = new JSONObject();
        respB.put("event", "start-matching");
        respB.put("opponent_username", a.getUsername());
        respB.put("opponent_photo", a.getPhoto());
        respB.put("game", respGame);
        // 给玩家B客户端返回结果
        if (null != users.get(b.getId())) {
            users.get(b.getId()).sendMessage(respB.toJSONString());
        }
    }
    
    private void startMatching() {
        System.out.println("start_matching!");
        MultiValueMap<String, String> playerData = new LinkedMultiValueMap<>();
        playerData.add("userId", this.user.getId().toString());
        playerData.add("rating", this.user.getRating().toString());

        restTemplate.postForObject(ADD_PLAYER_URL, playerData, String.class);

    }

    private void stopMatching() {
        System.out.println("stop_matching!");
        MultiValueMap<String, String> playerData = new LinkedMultiValueMap<>();
        playerData.add("userId", this.user.getId().toString());

        restTemplate.postForObject(REMOVE_PLAYER_URL, playerData, String.class);
    }

    private void move(int direction) {
        if (game.getPlayerA().getId().equals(user.getId())) {
            System.out.println("A的方向：" + direction);
            game.setNextStepA(direction);
        } else if (game.getPlayerB().getId().equals(user.getId())) {
            System.out.println("B的方向：" + direction);
            game.setNextStepB(direction);
        }
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        // 从 Client 接收消息
        System.out.println("receive message!");
        JSONObject data = JSON.parseObject(message);
        String event = data.getString("event");
        
        if ("start-matching".equals(event)) {
            startMatching();
        } else if ("stop-matching".equals(event)) {
            stopMatching();
        } else if ("move".equals(event)) {
            System.out.println("收到玩家" + user.getUsername() + "的移动指令！");
            move(data.getInteger("direction"));
        }
    }

    public void sendMessage(String message) {
        // 从 server 发送消息
        synchronized (this.session) {
            try {
                this.session.getBasicRemote().sendText(message);
            } catch (IOException e)  {
                e.printStackTrace();
            }
        }
    }

    @OnError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
    }
}
