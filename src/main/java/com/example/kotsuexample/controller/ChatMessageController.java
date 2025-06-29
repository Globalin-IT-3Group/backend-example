package com.example.kotsuexample.controller;

import com.example.kotsuexample.config.CurrentUser;
import com.example.kotsuexample.dto.ChatMessageDTO;
import com.example.kotsuexample.dto.GroupChatMessageDTO;
import com.example.kotsuexample.service.ChatMessageService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat-message")
public class ChatMessageController {

    private final ChatMessageService chatMessageService;

    @GetMapping("/rooms/{roomId}/messages")
    public ResponseEntity<List<ChatMessageDTO>> getChatMessages(@CurrentUser Integer userId,
                                                                @PathVariable Integer roomId) {
        List<ChatMessageDTO> dtos = chatMessageService.getChatMessagesWithReadStatus(roomId, userId);
        return ResponseEntity.ok(dtos);
    }

    // 그룹(스터디방) 채팅 메시지 조회
    @GetMapping("/rooms/{roomId}/group-messages")
    public ResponseEntity<List<GroupChatMessageDTO>> getGroupChatMessages(
            @CurrentUser Integer userId,
            @PathVariable Integer roomId) {
        List<GroupChatMessageDTO> dtos = chatMessageService.getGroupMessagesWithUnreadCount(roomId, userId);
        return ResponseEntity.ok(dtos);
    }

    // 읽음 처리 API
    @PostMapping("/rooms/{roomId}/read")
    public ResponseEntity<Void> markMessagesAsRead(@CurrentUser Integer userId,
                                                   @PathVariable Integer roomId,
                                                   @RequestBody(required = false) ChatReadRequest req) throws JsonProcessingException {
        // req.lastReadAt: 클라이언트가 전달하는 마지막 읽은 메시지 시간 (옵션, 없으면 서버 now())
        LocalDateTime lastReadAt = (req != null && req.getLastReadAt() != null) ? req.getLastReadAt() : LocalDateTime.now();
        chatMessageService.markMessagesAsRead(roomId, userId, lastReadAt);
        return ResponseEntity.ok().build();
    }

    @Getter
    @Setter
    public static class ChatReadRequest {
        private LocalDateTime lastReadAt;
    }
}
