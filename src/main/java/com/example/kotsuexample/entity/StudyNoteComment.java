package com.example.kotsuexample.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "study_note_comments")
@Getter
@NoArgsConstructor
public class StudyNoteComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // 어떤 노트에 달린 댓글인지
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_note_id", nullable = false)
    private StudyNote studyNote;

    // 댓글 작성자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 부모 댓글 (대댓글 지원)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comment_id")
    private StudyNoteComment parentComment;

    @Column(nullable = false)
    private String content;

    @Column(name = "is_secret", nullable = false)
    private boolean isSecret = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Builder
    public StudyNoteComment(StudyNote studyNote, User user, StudyNoteComment parentComment, String content, boolean isSecret, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.studyNote = studyNote;
        this.user = user;
        this.parentComment = parentComment;
        this.content = content;
        this.isSecret = isSecret;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public void updateContentAndSecretAndUpdatedAt(String content, boolean isSecret, LocalDateTime updatedAt) {
        this.content = content;
        this.isSecret = isSecret;
        this.updatedAt = updatedAt;
    }
}
