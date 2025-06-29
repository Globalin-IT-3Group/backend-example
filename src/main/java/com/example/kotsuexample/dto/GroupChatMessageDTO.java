package com.example.kotsuexample.dto;

import com.example.kotsuexample.entity.enums.MessageType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupChatMessageDTO {
    private Integer id;               // 메시지 PK (일반 메시지면 값 존재)
    private Integer chatRoomId;
    private Integer senderId;
    private String senderNickname;
    private String senderProfileImage;
    private String message;
    private MessageType messageType;  // "TEXT", "READ", ...
    private String sentAt;
    private Integer unreadCount;      // "READ" 이벤트면 이 필드만 있음
    private Integer messageId;        // "READ" 이벤트면 이 필드만 있음
    private String lastReadAt;        // "READ" 이벤트면 이 필드만 있음
}
