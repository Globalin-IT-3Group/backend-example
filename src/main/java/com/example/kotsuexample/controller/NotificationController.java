package com.example.kotsuexample.controller;

import com.example.kotsuexample.config.CurrentUser;
import com.example.kotsuexample.dto.NotificationResponse;
import com.example.kotsuexample.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notification")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/")
    public ResponseEntity<List<NotificationResponse>> getAllNotifications(@CurrentUser Integer userId) {
        List<NotificationResponse> responses = notificationService.getNotifications(userId);
        return ResponseEntity.ok(responses);
    }

    @DeleteMapping("/{notificationId}")
    public ResponseEntity<Void> deleteNotification(@CurrentUser Integer userId, @PathVariable Integer notificationId) {
        notificationService.deleteNotification(userId, notificationId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<Void> markNotificationAsRead(@CurrentUser Integer userId,
                                                       @PathVariable Integer notificationId) {
        notificationService.markAsRead(userId, notificationId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/read-all")
    public ResponseEntity<Void> markAllNotificationsAsRead(@CurrentUser Integer userId) {
        notificationService.markAllAsRead(userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Integer> getUnreadNotificationCount(@CurrentUser Integer userId) {
        int count = notificationService.countUnread(userId);
        return ResponseEntity.ok(count);
    }
}
