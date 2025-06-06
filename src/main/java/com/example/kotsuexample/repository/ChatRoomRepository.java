package com.example.kotsuexample.repository;

import com.globalin.kotsukotsu.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Integer> {
}
