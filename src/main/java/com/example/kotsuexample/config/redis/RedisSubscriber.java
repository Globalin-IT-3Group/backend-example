package com.example.kotsuexample.config.redis;

import com.example.kotsuexample.config.websocket.ChatSessionManager;
import com.example.kotsuexample.dto.GroupChatMessageDTO;
import com.example.kotsuexample.dto.SseNotificationDTO;
import com.example.kotsuexample.dto.UserResponse;
import com.example.kotsuexample.entity.ChatMessage;
import com.example.kotsuexample.entity.enums.ChatRoomType;
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
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        String channel = new String(message.getChannel(), StandardCharsets.UTF_8);
        String roomId = channel.split(":")[1];
        String payload = new String(message.getBody(), StandardCharsets.UTF_8);

        try {
            // 무조건 GroupChatMessageDTO로 파싱 (TEXT/READ 구분)
            GroupChatMessageDTO dto = objectMapper.readValue(payload, GroupChatMessageDTO.class);

            if (dto.getChatRoomId() == null) {
                return;
            }

            ChatRoomType roomType = chatRoomService.getRoomType(dto.getChatRoomId());

            if (roomType == ChatRoomType.GROUP) {
                handleGroupChatMessage(roomId, dto);
            } else {
                handleSingleChatMessage(roomId, dto);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 1:1 채팅 - 필요한 경우만 GroupChatMessageDTO에서 값 읽음
    private void handleSingleChatMessage(String roomId, GroupChatMessageDTO dto) {
        if (dto.getMessageType() == MessageType.READ) {
            // 1:1 채팅 읽음 처리 (단순화)
            chatReadService.markChatAsRead(dto.getChatRoomId(), dto.getSenderId(),
                    dto.getLastReadAt() != null ? OffsetDateTime.parse(dto.getLastReadAt()).toLocalDateTime() : LocalDateTime.now()
            );
            broadcast(roomId, toJson(dto));
            return;
        }

        // 일반 메시지 저장
        LocalDateTime kst = parseKst(dto.getSentAt());
        ChatMessage chatMessage = ChatMessage.builder()
                .chatRoomId(dto.getChatRoomId())
                .senderId(dto.getSenderId())
                .messageType(dto.getMessageType())
                .message(dto.getMessage())
                .sentAt(kst)
                .build();
        chatMessageRepository.save(chatMessage);

        broadcast(roomId, toJson(dto));

        // TODO: 일단 대기 이 코드는
        // SSE 알림 발송
        List<Integer> otherUserIds = getOtherUserIdsInRoom(dto.getChatRoomId(), dto.getSenderId());
        for (Integer targetId : otherUserIds) {
            boolean connected = sessionManager.isUserConnected(roomId, targetId);
            if (!connected) {
                UserResponse sender = userService.getSimpleUserInfoById(dto.getSenderId());
                SseNotificationDTO notification = SseNotificationDTO.builder()
                        .type(NotificationType.CHAT)
                        .sender(sender)
                        .roomId(dto.getChatRoomId())
                        .createdAt(LocalDateTime.now())
                        .build();
                sseService.send(targetId, NotificationType.CHAT, notification);
            }
        }
    }

    // 그룹(스터디) 채팅 및 시그널링
    private void handleGroupChatMessage(String roomId, GroupChatMessageDTO dto) {

        if (dto.getMessageType() == MessageType.READ) {
            if (dto.getSenderId() == null || dto.getLastReadAt() == null || dto.getMessageId() == null) return;

            // 1. 마지막 메시지 id까지 모든 메시지의 unreadCount 계산
            List<ChatMessage> messages = chatMessageRepository.findByChatRoomIdOrderBySentAtAsc(dto.getChatRoomId());
            Map<Integer, Integer> unreadCounts = new HashMap<>();
            for (ChatMessage msg : messages) {
                if (msg.getId() <= dto.getMessageId()) {
                    int count = chatReadService.getUnreadMemberCountForMessage(dto.getChatRoomId(), msg.getId());
                    unreadCounts.put(msg.getId(), count);
                }
            }

            // 2. GroupChatMessageDTO로 빌드
            GroupChatMessageDTO readEvent = GroupChatMessageDTO.builder()
                    .messageType(MessageType.READ)
                    .messageId(dto.getMessageId())
                    .lastReadAt(dto.getLastReadAt())
                    .chatRoomId(dto.getChatRoomId())
                    .senderId(dto.getSenderId())
                    .unreadCounts(unreadCounts) // ⬅️ map 내려줌!
                    .build();
            broadcast(roomId, toJson(readEvent));
            return;
        }

        // 일반 메시지 저장
        LocalDateTime kst = parseKst(dto.getSentAt());
        ChatMessage chatMessage = ChatMessage.builder()
                .chatRoomId(dto.getChatRoomId())
                .senderId(dto.getSenderId())
                .messageType(dto.getMessageType())
                .message(dto.getMessage())
                .sentAt(kst)
                .build();
        chatMessageRepository.save(chatMessage);

        UserResponse sender = userService.getSimpleUserInfoById(dto.getSenderId());
        int unreadCount = chatReadService.getUnreadMemberCountForMessage(dto.getChatRoomId(), chatMessage.getId());

        GroupChatMessageDTO sendDto = GroupChatMessageDTO.builder()
                .id(chatMessage.getId())
                .chatRoomId(chatMessage.getChatRoomId())
                .senderId(sender.getId())
                .senderNickname(sender.getNickname())
                .senderProfileImage(sender.getProfileImage())
                .message(chatMessage.getMessage())
                .messageType(chatMessage.getMessageType())
                .sentAt(
                        chatMessage.getSentAt()
                                .atOffset(ZoneOffset.UTC)
                                .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                )
                .unreadCount(unreadCount)
                .build();

        broadcast(roomId, toJson(sendDto));
    }

    private LocalDateTime parseKst(String sentAt) {
        if (sentAt == null) return null;
        try {
            OffsetDateTime odt = OffsetDateTime.parse(sentAt);
            return odt.atZoneSameInstant(ZoneId.of("Asia/Seoul")).toLocalDateTime();
        } catch (Exception e) {
            // fallback
            return LocalDateTime.now();
        }
    }

    private String toJson(Object o) {
        try { return objectMapper.writeValueAsString(o); }
        catch (Exception e) { return ""; }
    }

    // 세션 전체에 메시지 전파
    private void broadcast(String roomId, String payload) {
        for (WebSocketSession session : sessionManager.getSessions(roomId)) {
            try {
                if (session.isOpen()) {
                    // 동기화: 한 번에 한 쓰레드만 sendMessage!
                    synchronized (session) {
                        session.sendMessage(new TextMessage(payload));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                // 세션이 이미 죽었으면 map에서 제거 (선택)
                 sessionManager.removeSession(roomId, session);
            }
        }
    }


    private List<Integer> getOtherUserIdsInRoom(Integer roomId, Integer senderId) {
        return chatRoomService.getMemberIds(roomId).stream()
                .filter(id -> !id.equals(senderId))
                .toList();
    }
}
