package com.example.kotsuexample.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "study_rooms")
@Getter
@NoArgsConstructor
public class StudyRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String name;

    private String rule;
    private String notice;

    @Column(name = "user_count")
    private Integer userCount = 1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leader", nullable = false)
    private User leader;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
