package com.example.kotsuexample.repository;

import com.globalin.kotsukotsu.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Integer> {
}
