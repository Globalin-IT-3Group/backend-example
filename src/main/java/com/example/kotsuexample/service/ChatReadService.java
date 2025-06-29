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
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatReadService {

    private final ChatReadStatusRepository chatReadStatusRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final UserService userService;

    // 읽음 처리
    @Transactional
    public void markChatAsRead(Integer roomId, Integer userId, LocalDateTime lastReadAt) {
        ChatReadStatus status = chatReadStatusRepository
                .findByChatRoomIdAndUserId(roomId, userId)
                .orElse(ChatReadStatus.builder()
                        .chatRoomId(roomId)
                        .userId(userId)
                        .build());

        status.updateLastReadAt(lastReadAt);
        chatReadStatusRepository.save(status);
    }

    // 미확인 메시지 수 조회
    public int getUnreadCount(Integer roomId, Integer userId) {
        Optional<LocalDateTime> lastReadOpt = chatReadStatusRepository
                .findByChatRoomIdAndUserId(roomId, userId)
                .map(ChatReadStatus::getLastReadAt);

        if (lastReadOpt.isPresent()) {
            // 기존처럼 마지막 읽은 시각 이후 메시지 수 카운트
            return chatMessageRepository.countByChatRoomIdAndSentAtAfterAndSenderIdNot(
                    roomId, lastReadOpt.get(), userId
            );
        } else {
            // 한 번도 읽지 않은 경우 → 내가 보낸 것 빼고 전부 unread
            return chatMessageRepository.countByChatRoomIdAndSenderIdNot(
                    roomId, userId
            );
        }
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

    public int getUnreadMemberCountForMessage(Integer roomId, Integer messageId) {
        // 1. 메시지 조회
        ChatMessage message = chatMessageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("메시지를 찾을 수 없습니다."));

        // 2. 채팅방 모든 멤버 조회
        List<ChatRoomMember> members = chatRoomMemberRepository.findByChatRoomId(roomId);

        // 3. 각 멤버의 읽음 정보 조회 (Map<userId, lastReadAt>)
        Map<Integer, LocalDateTime> lastReadMap = chatReadStatusRepository
                .findByChatRoomId(roomId).stream()
                .collect(Collectors.toMap(ChatReadStatus::getUserId, ChatReadStatus::getLastReadAt));

        int unreadCount = 0;
        for (ChatRoomMember member : members) {
            Integer userId = member.getUserId();
            if (userId.equals(message.getSenderId())) continue; // 본인은 제외

            LocalDateTime lastReadAt = lastReadMap.get(userId);
            // lastReadAt이 없거나, 메시지보다 이전이면 아직 안 읽음
            if (lastReadAt == null || lastReadAt.isBefore(message.getSentAt())) {
                unreadCount++;
            }
        }
        return unreadCount;
    }
}
