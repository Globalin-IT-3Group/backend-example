package com.example.kotsuexample.dto.note;

import com.example.kotsuexample.entity.Note;
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

    public static NoteResponse fromEntity(Note note) {
        return NoteResponse.builder()
                .id(note.getId())
                .imageUrl(note.getImageUrl())
                .title(note.getTitle())
                .content(note.getContent())
                .createdAt(note.getCreatedAt())
                .build();
    }
}
