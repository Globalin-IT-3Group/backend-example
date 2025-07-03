package com.example.kotsuexample.controller;

import com.example.kotsuexample.config.CurrentUser;
import com.example.kotsuexample.dto.ChatRoomRequest;
import com.example.kotsuexample.dto.ChatRoomResponse;
import com.example.kotsuexample.dto.ChatRoomSummary;
import com.example.kotsuexample.dto.GroupChatRoomRequest;
import com.example.kotsuexample.service.ChatReadService;
import com.example.kotsuexample.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat-room")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;
    private final ChatReadService chatReadService;

    @PostMapping()
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

    @GetMapping("/{roomId}/summary")
    public ResponseEntity<ChatRoomSummary> getChatRoomSummary(@PathVariable Integer roomId,
                                                              @CurrentUser Integer userId) {
        ChatRoomSummary summary = chatReadService.getChatRoomSummary(roomId, userId);
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/summary/all")
    public ResponseEntity<List<ChatRoomSummary>> getAllChatRoomSummaries(@CurrentUser Integer userId) {
        List<ChatRoomSummary> summaries = chatReadService.getAllChatRoomSummaries(userId);
        return ResponseEntity.ok(summaries);
    }

    @PostMapping("/{roomId}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Integer roomId, @CurrentUser Integer userId) {
        chatReadService.markChatAsRead(roomId, userId, LocalDateTime.now());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/group")
    public ResponseEntity<ChatRoomResponse> getOrCreateGroupRoom(@RequestBody GroupChatRoomRequest req) {
        ChatRoomResponse response = chatRoomService.getOrCreateGroupRoom(req);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/study/{studyRoomId}/group-id")
    public ResponseEntity<Integer> getGroupChatRoomIdByStudyRoomId(@PathVariable Integer studyRoomId) {
        Integer chatRoomId = chatRoomService.getGroupChatRoomIdByStudyRoomId(studyRoomId);
        return ResponseEntity.ok(chatRoomId);
    }

    @GetMapping("/{chatRoomId}/study-id")
    public ResponseEntity<Integer> getStudyRoomIdByChatRoomId(@PathVariable Integer chatRoomId) {
        Integer studyRoomId = chatRoomService.getStudyRoomIdByChatRoomId(chatRoomId);
        return ResponseEntity.ok(studyRoomId);
    }
}
