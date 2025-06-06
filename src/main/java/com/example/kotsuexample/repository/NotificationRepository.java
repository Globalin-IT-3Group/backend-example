package com.example.kotsuexample.repository;

import com.example.kotsuexample.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {
}
