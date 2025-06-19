package com.example.kotsuexample.dto.study;

import com.example.kotsuexample.entity.StudyNoteComment;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class StudyNoteCommentDTO {
    private Integer id;
    private String content;
    private String writerName;
    private String writerProfileImageUrl;
    private boolean isSecret;
    private Integer parentCommentId;
    private LocalDateTime createdAt;

    public static StudyNoteCommentDTO fromEntity(StudyNoteComment comment) {
        return StudyNoteCommentDTO.builder()
                .id(comment.getId())
                .content(comment.isSecret() ? "비밀댓글입니다." : comment.getContent())
                .writerName(comment.getUser().getNickname())
                .writerProfileImageUrl(comment.getUser().getProfileImage())
                .isSecret(comment.isSecret())
                .parentCommentId(
                        comment.getParentComment() != null ? comment.getParentComment().getId() : null
                )
                .createdAt(comment.getCreatedAt())
                .build();
    }
}
