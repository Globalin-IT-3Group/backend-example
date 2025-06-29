package com.example.kotsuexample.dto;

import com.example.kotsuexample.entity.enums.MessageType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatReadEvent {
    private MessageType messageType;  // 항상 MessageType.READ
    private Integer messageId;        // 마지막 읽은 메시지 id
    private Integer unreadCount;      // 해당 메시지의 미확인 인원수
    private Integer userId;           // 읽은 사람의 id
    private String lastReadAt;        // 읽은 시각(ISO String)
    private Integer chatRoomId;
}
