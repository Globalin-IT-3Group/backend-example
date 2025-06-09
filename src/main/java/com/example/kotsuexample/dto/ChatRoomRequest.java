package com.example.kotsuexample.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomRequest {
    private Integer requesterId; // 채팅을 시작하는 사용자 ID
    private Integer targetId;    // 채팅 대상 친구의 사용자 ID
}
