package com.example.kotsuexample.service;

import com.example.kotsuexample.entity.Notification;
import com.example.kotsuexample.entity.enums.NotificationType;
import com.example.kotsuexample.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final SseService sseService;

    public void notifyFriendRequest(Integer receiverId, String content) {

        // DB에 저장
        Notification notification = Notification.builder()
                .userId(receiverId)
                .type(NotificationType.FRIEND)
                .content(content)
                .createdAt(LocalDateTime.now())
                .build();

        notificationRepository.save(notification);

        // 2. 실시간 전송 (SSE)
        sseService.send(receiverId, NotificationType.FRIEND, content);
    }

    public List<Notification> getNotifications(Integer userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public void deleteNotification(Integer userId, Integer notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("해당 알림이 존재하지 않습니다."));

        if (!notification.getUserId().equals(userId)) {
            throw new RuntimeException("해당 알림을 삭제할 권한이 없습니다.");
        }

        notificationRepository.delete(notification);
    }

}
