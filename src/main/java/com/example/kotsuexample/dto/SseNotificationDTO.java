package com.example.kotsuexample.dto;

import com.example.kotsuexample.entity.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SseNotificationDTO {
    private NotificationType type;
    private UserResponse sender;
    private String content;
    private Integer roomId; // 채팅방인 경우만 사용
    private LocalDateTime createdAt;
}

