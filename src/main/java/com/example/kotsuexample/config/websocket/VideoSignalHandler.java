package com.example.kotsuexample.config.websocket;

import com.example.kotsuexample.config.redis.RedisPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
@RequiredArgsConstructor
public class VideoSignalHandler extends TextWebSocketHandler {
    private final RedisPublisher redisPublisher;
    private final ChatSessionManager sessionManager;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String roomId = getRoomId(session);
        sessionManager.addSession(roomId, session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String roomId = getRoomId(session);
        sessionManager.removeSession(roomId, session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String roomId = getRoomId(session);
        redisPublisher.publish("signal:" + roomId, message.getPayload());
    }

    private String getRoomId(WebSocketSession session) {
        return sessionManager.getQueryParam(session, "roomId");
    }
    private String getUserId(WebSocketSession session) {
        return sessionManager.getQueryParam(session, "userId");
    }
}
