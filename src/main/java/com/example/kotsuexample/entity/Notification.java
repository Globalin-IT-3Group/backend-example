package com.example.kotsuexample.entity;

import com.example.kotsuexample.entity.enums.NotificationType;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Getter
@NoArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "sender_id", nullable = false)
    private Integer senderId; // ✅ 누가 보냈는지

    @Column(nullable = false)
    private NotificationType type;

    @Lob
    private String content;

    @Column(name = "is_read", nullable = false)
    private boolean isRead; // ✅ 읽음 여부

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public Notification(Integer userId, Integer senderId, NotificationType type, String content, boolean isRead, LocalDateTime createdAt) {
        this.userId = userId;
        this.senderId = senderId;
        this.type = type;
        this.content = content;
        this.isRead = isRead;
        this.createdAt = createdAt;
    }

    public void markAsRead() {
        this.isRead = true;
    }
}
