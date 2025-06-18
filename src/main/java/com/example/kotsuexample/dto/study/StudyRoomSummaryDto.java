package com.example.kotsuexample.dto.study;

import com.example.kotsuexample.entity.enums.StudyTag;
import lombok.Builder;
import lombok.Getter;

import java.util.Set;

@Getter
@Builder
public class StudyRoomSummaryDto {
    private Integer id;
    private String name;
    private String imageUrl;
    private Set<StudyTag> tags;
    private Integer currentMemberCount;
    private Integer maxUserCount;
}
