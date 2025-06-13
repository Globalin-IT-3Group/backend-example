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

    private String title;

    @Column(name = "study_explain", columnDefinition = "TEXT")
    private String studyExplain;

    // TODO: 멤버 카운트를 컬럼으로 갖고 있을까?

    @Column(name = "view_count", columnDefinition = "integer default 0")
    private Integer viewCount;

    @Column(name = "is_open", columnDefinition = "boolean default false")
    private Boolean isOpen;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
