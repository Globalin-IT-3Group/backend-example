package com.example.kotsuexample.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "notes")
@NoArgsConstructor
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    private String title;

    @Lob
    private String content;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Builder
    public Note(Integer userId, String title, String content, LocalDateTime createdAt) {
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
    }

    public void changeTitle(String title) {
        this.title = title;
    }

    public void changeContent(String content) {
        this.content = content;
    }
}
