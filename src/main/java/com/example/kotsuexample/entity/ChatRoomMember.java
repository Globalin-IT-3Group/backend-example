package com.example.kotsuexample.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_room_members")
@Getter
@NoArgsConstructor
public class ChatRoomMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "chat_room_id", nullable = false)
    private Integer chatRoomId;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "joined_at", updatable = false)
    private LocalDateTime joinedAt;

    @Builder
    public ChatRoomMember(Integer chatRoomId, Integer userId, LocalDateTime joinedAt) {
        this.chatRoomId = chatRoomId;
        this.userId = userId;
        this.joinedAt = joinedAt;
    }
}
