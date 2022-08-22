package com.kob.backend.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.kob.backend.config.RestTemplateConfig;
import com.kob.backend.consumer.util.Game;
import com.kob.backend.consumer.util.JwtAuthentication;
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
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
@ServerEndpoint("/websocket/{token}")  // 注意不要以'/'结尾
public class WebSocketServer {

    final public static ConcurrentHashMap<Integer, WebSocketServer> users = new ConcurrentHashMap<>();

    private User user;
    private Session session = null;

    private static UserMapper userMapper;
    public static RecordMapper recordMapper;
    private static RestTemplate restTemplate;
    private Game game = null;
    private final static String addPlayerUrl = "http://127.0.0.1:3001/player/add/";
    private final static String removePlayerUrl = "http://127.0.0.1:3001/player/remove/";

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
        System.out.println("connected!");
        int userId = JwtAuthentication.getUserId(token);
        this.user = userMapper.selectById(userId);

        if (this.user != null) {
            users.put(userId, this);
        } else {
            this.session.close();
        }
        System.out.println(users);
    }

    @OnClose
    public void onClose() {
        // 关闭链接
        System.out.println("disconnected!");
        if(this.user != null) {
            users.remove(this.user.getId());

        }
    }

    public static void startGame(Integer aId, Integer bId) {
        User a = userMapper.selectById(aId);
        User b = userMapper.selectById(bId);

        Game game = new Game(13, 14, 20, a.getId(), b.getId());

        if (users.get(a.getId()) != null)
            users.get(a.getId()).game = game;
        if (users.get(b.getId()) != null)
            users.get(b.getId()).game = game;
        game.createMap();
        game.start();

        JSONObject respGame = new JSONObject();
        respGame.put("a_id", game.getPlayerA().getId());
        respGame.put("a_sx", game.getPlayerA().getSx());
        respGame.put("a_sy", game.getPlayerA().getSy());
        respGame.put("b_id", game.getPlayerB().getId());
        respGame.put("b_sx", game.getPlayerB().getSx());
        respGame.put("b_sy", game.getPlayerB().getSy());
        respGame.put("map",game.getG());


        JSONObject respA = new JSONObject();
        respA.put("event","start-matching");
        respA.put("oppoent_username", b.getUsername());
        respA.put("oppoent_photo", b.getPhoto());
        respA.put("game", respGame);
        if (users.get(a.getId()) != null)
            users.get(a.getId()).sendMessage(respA.toJSONString());

        JSONObject respB = new JSONObject();
        respB.put("event","start-matching");
        respB.put("oppoent_username", a.getUsername());
        respB.put("oppoent_photo", a.getPhoto());
        respB.put("game", respGame);
        if (users.get(b.getId()) != null)
            users.get(b.getId()).sendMessage(respB.toJSONString());
    }

    private void startMatching() {
        System.out.println("start Matching");
        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("user_id", this.user.getId().toString());
        data.add("rating", this.user.getRating().toString());
        restTemplate.postForObject(addPlayerUrl, data, String.class);

    }

    private void stopMatching() {
        System.out.println("stop Matching");
        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("user_id", this.user.getId().toString());
        restTemplate.postForObject(removePlayerUrl, data, String.class);
    }

    private void move(int direction) {
        if (game.getPlayerA().getId().equals(user.getId())) {
            game.setNextStepA(direction);
        } else if (game.getPlayerB().getId().equals(user.getId())) {
            game.setNextStepB(direction);
        }
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        // 从Client接收消息
        System.out.println("receive message!");

        JSONObject data = JSONObject.parseObject(message);
        String event = data.getString("event");
        if("start-matching".equals(event)) {
            startMatching();
        } else if("stop-matching".equals(event)){
            stopMatching();
        } else if("move".equals(event)) {
            move(data.getInteger("direction"));
        }
    }

    @OnError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
    }

    public void sendMessage(String message) {
        // 向Client发送消息
        // 异步处理
        synchronized (this.session) {
            try {
                this.session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

