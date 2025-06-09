package com.example.kotsuexample.controller;

import com.example.kotsuexample.config.CurrentUser;
import com.example.kotsuexample.dto.ChatRoomRequest;
import com.example.kotsuexample.dto.ChatRoomResponse;
import com.example.kotsuexample.service.ChatReadService;
import com.example.kotsuexample.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat-room")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;
    private final ChatReadService chatReadService;

    @PostMapping("/chat/room")
    public ResponseEntity<ChatRoomResponse> getOrCreateSingleRoom(@RequestBody ChatRoomRequest request) {
        ChatRoomResponse response = chatRoomService.getOrCreateSingleRoom(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{roomId}/unread")
    public ResponseEntity<Integer> getUnreadCount(@PathVariable Integer roomId,
                                                  @CurrentUser Integer userId) {
        int unreadCount = chatReadService.getUnreadCount(roomId, userId);
        return ResponseEntity.ok(unreadCount);
    }
}
