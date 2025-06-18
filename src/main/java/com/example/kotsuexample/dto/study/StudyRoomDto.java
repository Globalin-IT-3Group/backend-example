package com.example.kotsuexample.dto.study;

import com.example.kotsuexample.entity.enums.StudyTag;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Builder
public class StudyRoomDto {
    private Integer id;
    private String name;
    private String rule;
    private String notice;
    private String imageUrl;
    private Integer maxUserCount;
    private Set<StudyTag> tags;
    private Integer leaderId;
    private LocalDateTime createdAt;
}
