package com.example.kotsuexample.dto.study;

import lombok.Getter;

@Getter
public class StudyNoteCreateDTO {
    private Integer studyRoomId;
    private String title;
    private String content;
    private String thumbnail;
}
