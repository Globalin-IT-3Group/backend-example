package com.example.kotsuexample.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "study_recruits")
@Getter
@NoArgsConstructor
public class StudyRecruit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "study_room_id", nullable = false)
    private Integer studyRoomId;

    @Lob
    private String explane;

    @Column(name = "is_open")
    private Boolean isOpen = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
