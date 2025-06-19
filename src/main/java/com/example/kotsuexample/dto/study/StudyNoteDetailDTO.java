package com.example.kotsuexample.dto.study;

import com.example.kotsuexample.entity.StudyNote;
import com.example.kotsuexample.entity.User;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class StudyNoteDetailDTO {
    private Integer id;
    private String title;
    private String content;
    private String thumbnail;
    private User user;
    private Integer heartCount;
    private List<StudyNoteCommentDTO> comments;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean hearted; // 내가 좋아요 눌렀는지

    public static StudyNoteDetailDTO fromEntity(StudyNote note, boolean hearted) {
        return StudyNoteDetailDTO.builder()
                .id(note.getId())
                .title(note.getTitle())
                .content(note.getContent())
                .thumbnail(note.getThumbnail())
                .user(note.getUser())
                .createdAt(note.getCreatedAt())
                .updatedAt(note.getUpdatedAt())
                .comments(
                        note.getComments()
                                .stream()
                                .map(StudyNoteCommentDTO::fromEntity)
                                .toList()
                )
                .heartCount(note.getHearts().size())
                .hearted(hearted)
                .build();
    }
}
