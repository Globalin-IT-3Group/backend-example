package com.example.kotsuexample.dto.study;

import com.example.kotsuexample.entity.enums.StudyTag;
import lombok.Builder;
import lombok.Getter;

import java.util.Set;

@Getter
@Builder
public class CreateStudyRoomRequest {
    private String name;
    private String rule;
    private String notice;
    private Integer maxUserCount;
    private Set<StudyTag> tags;
    private String imageUrl;
}
