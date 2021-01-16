package com.vsst.common.video;

import com.vsst.entity.ImageEncoder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import org.springframework.stereotype.Component;

import javax.websocket.*;
import java.io.IOException;
import java.nio.ByteBuffer;

@ClientEndpoint(encoders = {ImageEncoder.class})
@Component
@Slf4j
public class WebsocketClient {
    private  Session session;
    @OnOpen
    public void onOpen(Session session){
        log.info("Client WebSocket is opening...");
        this.session=session;
    }

    @OnMessage(maxMessageSize = 1048576L)
    public void onMessage(String message){
        log.info("接受服务端信息===="+message);

    }
    @OnError
    public void onError(Session session,Throwable error){
        log.error("client websocket错误！");
        error.printStackTrace();
    }

    @OnClose
    public void onClose() throws IOException {
        if(this.session.isOpen()){
            this.session.close();
        }
        log.info("关闭client websocket连接！");
    }

    public  void sendMessageByStr(String message){
        if (StringUtils.isNotBlank(message)) {
               session.getAsyncRemote().sendText(message);
                log.info("client 发送信息成功");
        }
    }

    public void sendMessageByObject(Object message) {
        if (message != null) {
            this.session.getAsyncRemote().sendObject(message);
        }
    }

    public void sendBinary(ByteBuffer message) {
        if (message != null) {
            this.session.getAsyncRemote().sendBinary(message);
        }
    }
}
