package com.example.kotsuexample.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_read_status")
@Getter
@NoArgsConstructor
public class ChatReadStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "chat_room_id", nullable = false)
    private Integer chatRoomId;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "last_read_at")
    private LocalDateTime lastReadAt;

    public void updateLastReadAt(LocalDateTime lastReadAt) {
        this.lastReadAt = lastReadAt;
    }

    @Builder
    public ChatReadStatus(Integer chatRoomId, Integer userId) {
        this.chatRoomId = chatRoomId;
        this.userId = userId;
    }
}
