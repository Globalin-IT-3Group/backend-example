package com.example.kotsuexample.dto;

import com.example.kotsuexample.entity.enums.MessageType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ChatMessageDTO {
    private Integer chatRoomId;
    private Integer senderId;
    private MessageType messageType; // TEXT, IMAGE, FILE 등
    private String message; // 텍스트 또는 파일 URL
    private LocalDateTime sentAt = LocalDateTime.now();
}
