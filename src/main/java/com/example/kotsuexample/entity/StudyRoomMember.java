package com.example.kotsuexample.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "study_room_memebers")
@NoArgsConstructor
public class StudyRoomMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "study_room_id", nullable = false)
    private Integer studyRoomId;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "joined_at", updatable = false)
    private LocalDateTime joinedAt;
}
