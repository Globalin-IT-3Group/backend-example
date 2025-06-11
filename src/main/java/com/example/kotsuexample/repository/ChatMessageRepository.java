package com.example.kotsuexample.repository;

import com.example.kotsuexample.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Integer> {
    List<ChatMessage> findByChatRoomIdOrderBySentAtAsc(Integer chatRoomId);
    int countByChatRoomIdAndSentAtAfterAndSenderIdNot(Integer chatRoomId, LocalDateTime sentAt, Integer senderId);

    Optional<ChatMessage> findTopByChatRoomIdOrderBySentAtDesc(Integer roomId);
}
