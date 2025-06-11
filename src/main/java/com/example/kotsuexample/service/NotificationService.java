package com.example.kotsuexample.service;

import com.example.kotsuexample.dto.NotificationResponse;
import com.example.kotsuexample.dto.SseNotificationDTO;
import com.example.kotsuexample.dto.UserResponse;
import com.example.kotsuexample.entity.Notification;
import com.example.kotsuexample.entity.enums.NotificationType;
import com.example.kotsuexample.exception.NotificationAccessDeniedException;
import com.example.kotsuexample.exception.NotificationNotFoundException;
import com.example.kotsuexample.repository.NotificationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final SseService sseService;
    private final UserService userService;

    public void notifyFriendRequest(Integer senderId, Integer receiverId, String content) {

        // 1. DB 저장
        Notification notification = Notification.builder()
                .userId(receiverId)
                .senderId(senderId) // ✅ 누가 보냈는지 저장
                .type(NotificationType.FRIEND)
                .content(content)
                .isRead(false) // ✅ 기본값 false
                .createdAt(LocalDateTime.now())
                .build();
        notificationRepository.save(notification);

        // 2. 실시간 전송 (SSE)
        UserResponse sender = userService.getSimpleUserInfoById(senderId);
        SseNotificationDTO dto = SseNotificationDTO.builder()
                .type(NotificationType.FRIEND)
                .sender(sender)
                .content(content)
                .createdAt(notification.getCreatedAt())
                .build();
        sseService.send(receiverId, NotificationType.FRIEND, dto);
    }

    public List<NotificationResponse> getNotifications(Integer userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(notification -> {
                    UserResponse sender = userService.getSimpleUserInfoById(notification.getSenderId());

                    return NotificationResponse.builder()
                            .id(notification.getId())
                            .type(notification.getType())
                            .content(notification.getContent())
                            .isRead(notification.isRead())
                            .createdAt(notification.getCreatedAt())
                            .sender(sender)
                            .build();
                })
                .toList();
    }

    public void deleteNotification(Integer userId, Integer notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotificationNotFoundException("알림이 존재하지 않습니다."));

        if (!notification.getUserId().equals(userId)) {
            throw new RuntimeException("해당 알림을 삭제할 권한이 없습니다.");
        }

        notificationRepository.delete(notification);
    }

    public void markAsRead(Integer userId, Integer notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotificationNotFoundException("알림이 존재하지 않습니다."));

        if (!notification.getUserId().equals(userId)) {
            throw new NotificationAccessDeniedException("알림을 읽을 권한이 없습니다.");
        }

        notification.markAsRead(); // 💡 도메인 로직 호출
        notificationRepository.save(notification); // dirty checking 적용
    }

    @Transactional
    public void markAllAsRead(Integer userId) {
        List<Notification> unreadNotifications = notificationRepository.findByUserIdAndIsReadFalse(userId);
        for (Notification notification : unreadNotifications) {
            notification.markAsRead();
        }
    }

    public int countUnread(Integer userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }
}
