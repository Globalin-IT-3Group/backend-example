package com.example.kotsuexample.controller;

import com.example.kotsuexample.config.CurrentUser;
import com.example.kotsuexample.config.websocket.ChatSessionManager;
import com.example.kotsuexample.service.ChatMessageService;
import com.example.kotsuexample.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/video-room")
public class VideoRoomController {
    private final ChatMessageService chatMessageService;
    private final UserService userService;
    private final ChatSessionManager sessionManager;

    // 🔵 화상방 입장 알림
    @PostMapping("/enter/{roomId}")
    public ResponseEntity<Void> enterVideoRoom(
            @CurrentUser Integer userId,
            @PathVariable Integer roomId) throws JsonProcessingException {
        String nickname = userService.getSimpleUserInfoById(userId).getNickname();
        String msg = nickname + "님이 화상채팅방에 입장했습니다.";
        chatMessageService.sendSystemMessageToGroup(roomId, msg);
        return ResponseEntity.ok().build();
    }

    // 🔴 화상방 퇴장 알림
    @PostMapping("/leave/{roomId}")
    public ResponseEntity<Void> leaveVideoRoom(
            @CurrentUser Integer userId,
            @PathVariable Integer roomId) throws JsonProcessingException {
        String nickname = userService.getSimpleUserInfoById(userId).getNickname();
        String msg = nickname + "님이 화상채팅방에서 퇴장했습니다.";
        chatMessageService.sendSystemMessageToGroup(roomId, msg);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/exists/{studyRoomId}")
    public ResponseEntity<Boolean> exists(@PathVariable String studyRoomId) {
        boolean hasActive = sessionManager.hasActiveSession(studyRoomId);
        return ResponseEntity.ok(hasActive);
    }
}
