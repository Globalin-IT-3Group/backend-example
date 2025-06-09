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

    @Column(nullable = false)
    private NotificationType type;

    @Lob
    private String content;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Builder

    public Notification(Integer userId, NotificationType type, String content, LocalDateTime createdAt) {
        this.userId = userId;
        this.type = type;
        this.content = content;
        this.createdAt = createdAt;
    }
}
