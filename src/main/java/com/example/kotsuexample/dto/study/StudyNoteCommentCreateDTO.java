package com.example.kotsuexample.dto.study;

import lombok.Getter;

@Getter
public class StudyNoteCommentCreateDTO {
    private String content;
    private boolean isSecret;
    private Integer parentCommentId;
}
