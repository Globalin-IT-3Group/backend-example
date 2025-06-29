package com.example.kotsuexample.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "study_notes")
@Getter
@NoArgsConstructor
public class StudyNote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_room_id", nullable = false)
    private StudyRoom studyRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Lob
    private String thumbnail;

    @Column(nullable = false)
    private String title;

    @Lob
    private String content;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "studyNote", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Heart> hearts = new ArrayList<>();

    @OneToMany(mappedBy = "studyNote", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StudyNoteComment> comments = new ArrayList<>();

    @Builder
    public StudyNote(String title, String content, String thumbnail, StudyRoom studyRoom, User user, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.title = title;
        this.content = content;
        this.thumbnail = thumbnail;
        this.studyRoom = studyRoom;
        this.user = user;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public void updateTitleAndContentAndThumbnailAndUpdatedAt(String title, String content, String thumbnail, LocalDateTime updatedAt) {
        this.title = title;
        this.content = content;
        this.thumbnail = thumbnail;
        this.updatedAt = updatedAt;
    }
}
