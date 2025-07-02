package com.example.kotsuexample.dto;

import com.example.kotsuexample.entity.enums.ChatRoomType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoomSummary {
    private Integer roomId;
    private int unreadCount;
    private String lastMessage;
    private LocalDateTime lastMessageAt;
    private List<UserResponse> otherUsers;
    private ChatRoomType roomType;
}
