package com.example.kotsuexample.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "study_room_members")
@NoArgsConstructor
public class StudyRoomMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_room_id", nullable = false)
    private StudyRoom studyRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "joined_at", updatable = false)
    private LocalDateTime joinedAt;

    @Builder
    public StudyRoomMember(StudyRoom studyRoom, User user, LocalDateTime joinedAt) {
        this.studyRoom = studyRoom;
        this.user = user;
        this.joinedAt = joinedAt;
    }

    public void setStudyRoom(StudyRoom studyRoom) {
        this.studyRoom = studyRoom;
    }
}
