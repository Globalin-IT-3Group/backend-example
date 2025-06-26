package com.example.kotsuexample.dto.note;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class NoteResponse {
    private Integer id;
    private String imageUrl;
    private String title;
    private String content;
    private LocalDateTime createdAt;
}
