package com.example.kotsuexample.dto;

import com.example.kotsuexample.entity.enums.NotificationType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class NotificationResponse {
    private Integer id;
    private NotificationType type;
    private String content;
    private boolean isRead;
    private LocalDateTime createdAt;
    private UserResponse sender;
}

