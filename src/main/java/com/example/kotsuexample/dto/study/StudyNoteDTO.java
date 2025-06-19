package com.example.kotsuexample.dto.study;

import com.example.kotsuexample.entity.StudyNote;
import com.example.kotsuexample.entity.User;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class StudyNoteDTO {
    private Integer id;
    private String title;
    private String thumbnail;
    private User writer; // 작성자를 User 객체로 바로 전달
    private Integer heartCount;
    private Integer commentCount;
    private LocalDateTime createdAt;

    public static StudyNoteDTO fromEntity(StudyNote note) {
        return StudyNoteDTO.builder()
                .id(note.getId())
                .title(note.getTitle())
                .thumbnail(note.getThumbnail())
                .writer(note.getUser())
                .createdAt(note.getCreatedAt())
                .heartCount(note.getHearts().size())
                .commentCount(note.getComments().size())
                .build();
    }
}
