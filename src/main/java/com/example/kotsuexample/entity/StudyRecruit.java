package com.example.kotsuexample.entity;

import com.example.kotsuexample.dto.study.StudyRecruitSaveRequestDTO;
import com.mysql.cj.MysqlConnection;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "study_recruits")
@Getter
@Setter
@NoArgsConstructor
public class StudyRecruit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_room_id", nullable = false, unique = true)
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

    public StudyRecruitSaveRequestDTO toStudyRecruitSaveRequestDTO(Integer studyRoomId) {
        return StudyRecruitSaveRequestDTO.builder()
                .id(this.id)
                .studyRoomId(studyRoomId)
                .isOpen(this.isOpen)
                .studyExplain(this.studyExplain)
                .title(this.title)
                .build();
    }

    public User getLeader() {
        return this.studyRoom.getLeader();
    }
}
