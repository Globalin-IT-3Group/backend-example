package com.example.kotsuexample.entity;

import com.example.kotsuexample.entity.enums.StudyRequestStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "study_requests")
@Getter
@NoArgsConstructor
public class StudyRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "study_recruit_id", nullable = false)
    private Integer studyRecruitId;

    @Lob
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StudyRequestStatus status = StudyRequestStatus.PENDING;

    @Column(name = "requested_at")
    private LocalDateTime requestedAt;
}
