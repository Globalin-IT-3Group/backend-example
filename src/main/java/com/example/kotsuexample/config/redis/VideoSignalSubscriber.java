package com.example.kotsuexample.config.redis;

import com.example.kotsuexample.config.websocket.ChatSessionManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class VideoSignalSubscriber implements MessageListener {

    private final ChatSessionManager sessionManager;

    @Override
    public void onMessage(org.springframework.data.redis.connection.Message message, byte[] pattern) {
        String channel = new String(message.getChannel(), StandardCharsets.UTF_8);
        String[] tokens = channel.split(":");
        String type = tokens[0];
        String roomId = tokens[1];
        String payload = new String(message.getBody(), StandardCharsets.UTF_8);

        if (!"signal".equals(type)) return;

        // payload는 OFFER/ANSWER/CANDIDATE 등 WebRTC용 JSON 그대로 브로드캐스트
        for (WebSocketSession session : sessionManager.getSessions(roomId)) {
            try {
                if (session.isOpen()) {
                    synchronized (session) {
                        session.sendMessage(new TextMessage(payload));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                sessionManager.removeSession(roomId, session);
            }
        }
    }
}
