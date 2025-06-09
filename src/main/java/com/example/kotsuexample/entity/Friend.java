package com.example.kotsuexample.entity;

import com.example.kotsuexample.entity.enums.FriendStatus;
import jakarta.persistence.*;
import lombok.Builder;
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
    private FriendStatus status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Builder
    public Friend(Integer requesterId, Integer addresseeId, FriendStatus status) {
        this.requesterId = requesterId;
        this.addresseeId = addresseeId;
        this.status = status;
        this.createdAt = LocalDateTime.now();
    }

    public void changeFriendStatus(FriendStatus status) {
        this.status = status;
    }
}
