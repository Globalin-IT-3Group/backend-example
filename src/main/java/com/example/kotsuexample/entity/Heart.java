package com.example.kotsuexample.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "study_note_hearts",
        uniqueConstraints = @UniqueConstraint(columnNames = {"study_note_id", "user_id"})
)
@Getter
@NoArgsConstructor
public class Heart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_note_id", nullable = false)
    private StudyNote studyNote;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Builder
    public Heart(StudyNote studyNote, User user) {
        this.studyNote = studyNote;
        this.user = user;
    }
}
