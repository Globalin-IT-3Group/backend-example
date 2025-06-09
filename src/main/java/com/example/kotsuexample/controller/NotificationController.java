package com.example.kotsuexample.controller;

import com.example.kotsuexample.config.CurrentUser;
import com.example.kotsuexample.entity.Notification;
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
    public ResponseEntity<List<Notification>> getAllNotifications(@CurrentUser Integer userId) {
        List<Notification> notifications = notificationService.getNotifications(userId);
        return ResponseEntity.ok(notifications);
    }

    @DeleteMapping("/{notificationId}")
    public ResponseEntity<Void> deleteNotification(@CurrentUser Integer userId, @PathVariable Integer notificationId) {
        notificationService.deleteNotification(userId, notificationId);
        return ResponseEntity.noContent().build();
    }
}
