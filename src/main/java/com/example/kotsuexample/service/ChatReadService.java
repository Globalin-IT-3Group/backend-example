package com.example.kotsuexample.service;

import com.example.kotsuexample.entity.ChatReadStatus;
import com.example.kotsuexample.repository.ChatMessageRepository;
import com.example.kotsuexample.repository.ChatReadStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ChatReadService {

    private final ChatReadStatusRepository chatReadStatusRepository;
    private final ChatMessageRepository chatMessageRepository;

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
}
