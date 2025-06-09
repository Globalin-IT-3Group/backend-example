package com.example.kotsuexample.config.redis;

import com.example.kotsuexample.config.websocket.ChatSessionManager;
import com.example.kotsuexample.dto.ChatMessageDTO;
import com.example.kotsuexample.entity.ChatMessage;
import com.example.kotsuexample.repository.ChatMessageRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class RedisSubscriber implements MessageListener {

    private final ChatSessionManager sessionManager;
    private final ObjectMapper objectMapper;
    private final ChatMessageRepository chatMessageRepository;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        // 1. Redis 채널명 가져오기 ("chatroom:123")
        String channel = new String(message.getChannel(), StandardCharsets.UTF_8);

        // 2. chatRoomId 추출 ("123")
        String roomId = channel.split(":")[1];

        // 3. 메시지 본문 가져오기
        String payload = new String(message.getBody(), StandardCharsets.UTF_8);

        try {
            // 4. JSON → DTO 역직렬화
            ChatMessageDTO dto = objectMapper.readValue(payload, ChatMessageDTO.class);

            // 5. DB에 채팅 내역 저장 (MySQL)
            ChatMessage chatMessage = ChatMessage.builder()
                    .chatRoomId(dto.getChatRoomId())
                    .senderId(dto.getSenderId())
                    .messageType(dto.getMessageType())
                    .message(dto.getMessage())
                    .sentAt(dto.getSentAt())
                    .build();

            chatMessageRepository.save(chatMessage);

            // 6. WebSocket을 통해 채팅방 사용자에게 전송
            for (WebSocketSession session : sessionManager.getSessions(roomId)) {
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(payload));
                }
            }

        } catch (Exception e) {
            e.printStackTrace(); // 또는 로깅
        }
    }
}
