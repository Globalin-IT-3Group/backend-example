package com.example.kotsuexample.repository;

import com.example.kotsuexample.entity.ChatReadStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatReadStatusRepository extends JpaRepository<ChatReadStatus, Integer> {
    Optional<ChatReadStatus> findByChatRoomIdAndUserId(Integer chatRoomId, Integer userId);
}
