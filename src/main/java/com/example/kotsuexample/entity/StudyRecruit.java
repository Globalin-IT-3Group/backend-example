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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_room_id", nullable = false)
    private StudyRoom studyRoom;

    private String title;

    @Column(name = "study_explain", columnDefinition = "TEXT")
    private String studyExplain;

    @Column(name = "view_count", columnDefinition = "integer default 0")
    private Integer viewCount;

    @Column(name = "is_open", columnDefinition = "boolean default false")
    private Boolean isOpen;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
