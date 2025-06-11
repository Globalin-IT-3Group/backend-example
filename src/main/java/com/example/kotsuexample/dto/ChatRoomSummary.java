package com.example.kotsuexample.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoomSummary {
    private Integer roomId;
    private int unreadCount;
    private String lastMessage;
    private LocalDateTime lastMessageAt;
    private UserResponse otherUser;
}
