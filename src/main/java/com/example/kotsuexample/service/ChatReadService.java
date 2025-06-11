package com.example.kotsuexample.service;

import com.example.kotsuexample.dto.ChatRoomSummary;
import com.example.kotsuexample.dto.UserResponse;
import com.example.kotsuexample.entity.ChatMessage;
import com.example.kotsuexample.entity.ChatReadStatus;
import com.example.kotsuexample.entity.ChatRoomMember;
import com.example.kotsuexample.repository.ChatMessageRepository;
import com.example.kotsuexample.repository.ChatReadStatusRepository;
import com.example.kotsuexample.repository.ChatRoomMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatReadService {

    private final ChatReadStatusRepository chatReadStatusRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final UserService userService;

    // 읽음 처리
    @Transactional
    public void markChatAsRead(Integer roomId, Integer userId) {
        ChatReadStatus status = chatReadStatusRepository
                .findByChatRoomIdAndUserId(roomId, userId)
                .orElse(ChatReadStatus.builder()
                        .chatRoomId(roomId)
                        .userId(userId)
                        .build());

        status.updateLastReadAt(LocalDateTime.now());
        chatReadStatusRepository.save(status);
    }

    // 미확인 메시지 수 조회
    public int getUnreadCount(Integer roomId, Integer userId) {
        LocalDateTime lastRead = chatReadStatusRepository
                .findByChatRoomIdAndUserId(roomId, userId)
                .map(ChatReadStatus::getLastReadAt)
                .orElse(LocalDateTime.MIN);

        return chatMessageRepository.countByChatRoomIdAndSentAtAfterAndSenderIdNot(
                roomId, lastRead, userId);
    }

    public ChatRoomSummary getChatRoomSummary(Integer roomId, Integer userId) {
        int unread = getUnreadCount(roomId, userId);
        Optional<ChatMessage> lastMessage = chatMessageRepository.findTopByChatRoomIdOrderBySentAtDesc(roomId);

        // ✅ 상대방 찾기 (1:1 채팅 기준)
        Integer otherUserId = chatRoomMemberRepository
                .findByChatRoomId(roomId).stream()
                .map(ChatRoomMember::getUserId)
                .filter(id -> !id.equals(userId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("상대방을 찾을 수 없습니다."));

        UserResponse otherUser = userService.getSimpleUserInfoById(otherUserId);

        return ChatRoomSummary.builder()
                .roomId(roomId)
                .unreadCount(unread)
                .lastMessage(lastMessage.map(ChatMessage::getMessage).orElse(""))
                .lastMessageAt(lastMessage.map(ChatMessage::getSentAt).orElse(null))
                .otherUser(otherUser)
                .build();
    }

    public List<ChatRoomSummary> getAllChatRoomSummaries(Integer userId) {
        return chatRoomMemberRepository.findByUserId(userId).stream()
                .map(member -> getChatRoomSummary(member.getChatRoomId(), userId))
                .toList();
    }
}
