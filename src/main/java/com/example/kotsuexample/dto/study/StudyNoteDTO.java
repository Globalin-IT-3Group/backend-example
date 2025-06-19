package com.example.kotsuexample.dto.study;

import com.example.kotsuexample.dto.UserResponse;
import com.example.kotsuexample.entity.StudyNote;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class StudyNoteDTO {
    private Integer id;
    private String title;
    private String thumbnail;
    private UserResponse user; // 작성자를 User 객체로 바로 전달
    private Integer heartCount;
    private Integer commentCount;
    private boolean hearted;
    private String content;
    private LocalDateTime createdAt;

    public static StudyNoteDTO fromEntity(StudyNote note, boolean hearted) {
        return StudyNoteDTO.builder()
                .id(note.getId())
                .title(note.getTitle())
                .thumbnail(note.getThumbnail())
                .user(note.getUser().toUserResponse())
                .createdAt(note.getCreatedAt())
                .heartCount(note.getHearts().size())
                .commentCount(note.getComments().size())
                .content(note.getContent())
                .hearted(hearted)
                .build();
    }
}
