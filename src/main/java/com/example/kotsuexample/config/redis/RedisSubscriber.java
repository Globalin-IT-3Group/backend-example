package com.example.kotsuexample.config.redis;

import com.example.kotsuexample.config.websocket.ChatSessionManager;
import com.example.kotsuexample.dto.ChatMessageDTO;
import com.example.kotsuexample.dto.SseNotificationDTO;
import com.example.kotsuexample.dto.UserResponse;
import com.example.kotsuexample.entity.ChatMessage;
import com.example.kotsuexample.entity.enums.MessageType;
import com.example.kotsuexample.entity.enums.NotificationType;
import com.example.kotsuexample.repository.ChatMessageRepository;
import com.example.kotsuexample.service.ChatReadService;
import com.example.kotsuexample.service.ChatRoomService;
import com.example.kotsuexample.service.SseService;
import com.example.kotsuexample.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RedisSubscriber implements MessageListener {

    private final ChatSessionManager sessionManager;
    private final ObjectMapper objectMapper;
    private final ChatMessageRepository chatMessageRepository;
    private final SseService sseService;
    private final ChatRoomService chatRoomService;
    private final UserService userService;
    private final ChatReadService chatReadService;

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

            OffsetDateTime odt = OffsetDateTime.parse(dto.getSentAt());
            LocalDateTime kst = odt.atZoneSameInstant(ZoneId.of("Asia/Seoul")).toLocalDateTime();

            if (dto.getMessageType() == MessageType.READ) {
                chatReadService.markChatAsRead(dto.getChatRoomId(), dto.getSenderId(), kst);
                // 그대로 다른 사용자들에게 전파
                for (WebSocketSession session : sessionManager.getSessions(roomId)) {
                    if (session.isOpen()) {
                        session.sendMessage(new TextMessage(payload));
                    }
                }
                return;
            }

            // 5. DB에 채팅 내역 저장 (MySQL)
            ChatMessage chatMessage = ChatMessage.builder()
                    .chatRoomId(dto.getChatRoomId())
                    .senderId(dto.getSenderId())
                    .messageType(dto.getMessageType())
                    .message(dto.getMessage())
                    .sentAt(kst) // <= 변환!
                    .build();

            chatMessageRepository.save(chatMessage);

            // 6. WebSocket을 통해 채팅방 사용자에게 전송
            for (WebSocketSession session : sessionManager.getSessions(roomId)) {
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(payload));
                }
            }

            // 알림 발송 (상대방이 연결 안 되어 있을 경우)
            List<Integer> otherUserIds = getOtherUserIdsInRoom(dto.getChatRoomId(), dto.getSenderId());
            for (Integer targetId : otherUserIds) {
                boolean connected = sessionManager.isUserConnected(roomId, targetId);
                if (!connected) {
                    UserResponse sender = userService.getSimpleUserInfoById(dto.getSenderId());

                    SseNotificationDTO notification = SseNotificationDTO.builder()
                            .type(NotificationType.CHAT)
                            .sender(sender)
                            .roomId(dto.getChatRoomId())
                            .createdAt(kst)
                            .build();

                    sseService.send(targetId, NotificationType.CHAT, notification);
                }
            }
        } catch (Exception e) {
            e.printStackTrace(); // 또는 로깅
        }
    }

    private List<Integer> getOtherUserIdsInRoom(Integer roomId, Integer senderId) {
        return chatRoomService.getMemberIds(roomId).stream()
                .filter(id -> !id.equals(senderId))
                .toList();
    }
}
