package com.example.kotsuexample.controller;

import com.example.kotsuexample.config.CurrentUser;
import com.example.kotsuexample.dto.ChatMessageDTO;
import com.example.kotsuexample.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
