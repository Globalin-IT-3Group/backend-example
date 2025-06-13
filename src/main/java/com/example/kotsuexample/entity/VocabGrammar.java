package com.example.kotsuexample.entity;

import com.example.kotsuexample.entity.enums.EntryType;
import com.example.kotsuexample.entity.enums.ExamType;
import com.example.kotsuexample.entity.enums.Level;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "vocab_grammar")
@Getter @Setter
@NoArgsConstructor
public class VocabGrammar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private EntryType type;          // WORD, GRAMMAR

    @Enumerated(EnumType.STRING)
    @Column(name = "level", nullable = false)
    private Level level;             // N1, N2, ...

    @Enumerated(EnumType.STRING)
    @Column(name = "exam_type", nullable = false)
    private ExamType examType;       // JLPT, JPT, TOPIK

    @Column(name = "jp_word", length = 255)
    private String jpWord;

    @Column(name = "hiragana", length = 255)
    private String hiragana;

    @Column(name = "alt_form", columnDefinition = "TEXT")
    private String altForm; // 이형/다른 표기들(쉼표, 슬래시 등으로 구분해서 여러 개 저장)

    @Column(name = "pos", length = 64)
    private String pos;

    @Column(columnDefinition = "TEXT")
    private String meaning;     // '뜻'만 or 원본 전체

    @Column(columnDefinition = "TEXT")
    private String example;     // 예문만(분리 가능하면)

    @Column(columnDefinition = "TEXT")
    private String synonym;     // 동의어만(분리 가능하면)

    @Column(name = "created_at")
    private java.time.LocalDateTime createdAt;
}
