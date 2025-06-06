package com.example.kotsuexample.entity;

import com.example.kotsuexample.entity.enums.ChatRoomType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_rooms")
@Getter
@NoArgsConstructor
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private ChatRoomType type;

    @Column(name = "study_room_id")
    private Long studyRoomId;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
