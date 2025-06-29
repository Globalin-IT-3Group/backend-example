package com.example.kotsuexample.dto;

import com.example.kotsuexample.entity.enums.MessageType;
import lombok.*;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageDTO {
    private Integer id;
    private Integer chatRoomId;
    private Integer senderId;
    private String message;
    private MessageType messageType;
    private String sentAt;
    private Boolean isRead; // (옵션)

    // === 읽음 이벤트 전용 ===
    private String lastReadAt;      // 읽음 이벤트일 때만 사용
    private Integer messageId;      // 읽음 이벤트일 때만 사용 (읽은 대상 메시지의 id)
}
