package com.example.kotsuexample.dto;

import com.example.kotsuexample.entity.ChatRoom;
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
public class ChatRoomResponse {

    private Integer roomId;
    private ChatRoomType type; // SINGLE or GROUP
    private List<MemberInfo> members;
    private LocalDateTime createdAt;

    // 내부 클래스 또는 별도 DTO
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MemberInfo {
        private Integer userId;
        private String nickname;
        private String profileImageUrl;
    }

    public static ChatRoomResponse of(ChatRoom room, List<MemberInfo> members) {
        return ChatRoomResponse.builder()
                .roomId(room.getId())
                .type(room.getType())
                .createdAt(room.getCreatedAt())
                .members(members)
                .build();
    }
}
