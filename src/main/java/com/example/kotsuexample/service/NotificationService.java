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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final SseService sseService;
    private final UserService userService;

    public void sseNotifyRequest(Integer senderId, Integer receiverId, String content, NotificationType type) {
        // 1. DB 저장
        Notification notification = Notification.builder()
                .userId(receiverId)
                .senderId(senderId) // ✅ 누가 보냈는지 저장
                .type(type)
                .content(content)
                .isRead(false) // ✅ 기본값 false
                .createdAt(LocalDateTime.now())
                .build();
        notificationRepository.save(notification);

        // 2. 실시간 전송 (SSE)
        UserResponse sender = userService.getSimpleUserInfoById(senderId);
        SseNotificationDTO dto = SseNotificationDTO.builder()
                .type(type)
                .sender(sender)
                .content(content)
                .createdAt(notification.getCreatedAt())
                .build();
        sseService.send(receiverId, type, dto);
    }

    public void deleteNotification(Integer userId, Integer notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotificationNotFoundException("알림이 존재하지 않습니다."));

        if (!notification.getUserId().equals(userId)) {
            throw new NotificationAccessDeniedException("해당 알림을 삭제할 권한이 없습니다.");
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

    public Page<NotificationResponse> getNotificationsPage(Integer userId, int page, int size, String sort) {
        String[] sortArr = sort.split(",");
        Sort.Direction direction = sortArr.length > 1 && sortArr[1].equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(direction, sortArr[0]));

        // JPA에서 Page<Notification> 조회 → DTO 변환
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageRequest)
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
                });
    }
}
