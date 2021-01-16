package com.vsst.common.video;

import cn.hutool.core.codec.Base64;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.vsst.entity.Image;
import com.vsst.entity.ImageEncoder;
import javassist.bytecode.ByteArray;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;


import javax.imageio.ImageIO;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.*;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
@ServerEndpoint(value = "/video/websocket/{userId}",encoders = {ImageEncoder.class})
@Slf4j
public class WebSocketServer {
//    /**
//     * 静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
//     */
//    private static int onlineCount = 0;
    private Session session;
    private String userId= " ";
//    private static CopyOnWriteArraySet<WebSocketServer> webSocketServers =new CopyOnWriteArraySet<>();
//
//    private static Map<String,Session> sessionPool = new HashMap<>();
    /**
     * concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
     */
    private static ConcurrentHashMap<String, WebSocketServer> webSocketMap = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("userId") String userId) {
        this.session = session;
        this.userId = userId;
        if (webSocketMap.contains(userId)){
            webSocketMap.remove(userId);
            webSocketMap.put(userId,this);
        }else {
            webSocketMap.put(userId,this);
        }

        log.info("【websocket消息】有新的连接，用户id: {} |总数为:"+ webSocketMap.size(),userId);
    }

    @OnClose
    public void onClose() {
        webSocketMap.remove(userId);
        log.info("【websocket消息】用户id：{} 连接断开，总数为:"+ webSocketMap.size(),userId);
    }

    @OnMessage(maxMessageSize = 1048576L)
    public void onTextMessage(Session session, String message){
        if (webSocketMap.containsKey("steven")&& webSocketMap.containsKey("wlc")){
            synchronized (session){
                synchronized (webSocketMap.get("steven").session){
                    if (webSocketMap.get("steven").session==session){
                        webSocketMap.get("wlc").session.getAsyncRemote().sendText(message);
//                        log.info("【websocket消息】收到客户端Steven的String消息长度:"+message.length());
                    }
                    if (webSocketMap.get("wlc").session==session){
                        webSocketMap.get("steven").session.getAsyncRemote().sendText(message);
//                        log.info("【websocket消息】收到客户端wlc的String消息长度:"+message.length());
                    }
                }

            }
        }

    }
    @OnMessage()
    public void onByteMessage(Session session, ByteBuffer message){
        log.info("【websocket消息】收到客户端ByteBuffer消息:"+message.toString());

    }
    /*
    暂时用不上，可不看，用于Ping/Pong包的发送
     */
    @OnMessage
    public void onPongMessage(Session session, PongMessage message){
        log.info("【websocket消息】收到客户端PongMessage消息:"+message.getApplicationData().toString());
    }

    // 此为广播消息,文本信息
    public static void sendAllByStr(String message) {
        for(WebSocketServer webSocketServer : webSocketMap.values()) {
            try {
                synchronized (webSocketServer.session){
                    webSocketServer.session.getAsyncRemote().sendText(message);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // 此为单点消息 (发送文本)
    public static void sendByStr(String userId, String message) {
        Session session = webSocketMap.get(userId).session;
        if (session != null) {
            synchronized (session){
                session.getAsyncRemote().sendText(message);
            }
//            try {
//                session.getBasicRemote().sendText(message);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
        }else {
            log.error("用户" + userId + ",不在线！");
        }
    }
    // 此为广播消息(发送对象)
    public static void sendAllByObject(Object message) {
        for(WebSocketServer webSocketServer : webSocketMap.values()) {

            try {
                webSocketServer.session.getAsyncRemote().sendObject(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    // 此为单点消息 (发送对象)
    public static void sendByObject(String userId, Object message) {
        if (webSocketMap.containsKey(userId)){
            Session session = webSocketMap.get(userId).session;
            if (session != null&&session.isOpen()) {
                session.getAsyncRemote().sendObject(message);
            }else {
                log.error("用户" + userId + ",不在线！");
            }
        }

    }


//    public static synchronized int getOnlineCount() {
//        return onlineCount;
//    }
//
//    public static synchronized void addOnlineCount() {
//        WebSocketServer.onlineCount++;
//    }
//
//    public static synchronized void subOnlineCount() {
//        WebSocketServer.onlineCount--;
//    }
}
