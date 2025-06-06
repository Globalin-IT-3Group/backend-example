package com.example.kotsuexample.entity;

import com.example.kotsuexample.entity.enums.FriendStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "friends")
@NoArgsConstructor
public class Friend {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "requester_id", nullable = false)
    private Integer requesterId;

    @Column(name = "addressee_id", nullable = false)
    private Integer addresseeId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FriendStatus status = FriendStatus.PENDING;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
