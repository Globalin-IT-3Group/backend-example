package com.example.kotsuexample.repository;

import com.example.kotsuexample.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    Page<Notification> findByUserIdOrderByCreatedAtDesc(Integer userId, PageRequest pageRequest);

    List<Notification> findByUserIdAndIsReadFalse(Integer userId);

    int countByUserIdAndIsReadFalse(Integer userId);
}
