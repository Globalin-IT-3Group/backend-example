package com.example.kotsuexample.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter; // <-- 필요시 추가

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter // 필요시
@Entity
@Table(name = "boards")
@NoArgsConstructor
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String title;

    @Lob
    private String content;

    @Column(name = "view_count")
    private Integer viewCount = 0;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // 양방향 연관관계 (게시글 → 댓글 목록)
    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Comment> comments = new ArrayList<>();

    public void addComment(Comment comment) {
        comments.add(comment);
        comment.setBoard(this);
    }
}
