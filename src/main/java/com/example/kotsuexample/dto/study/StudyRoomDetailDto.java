package com.example.kotsuexample.dto.study;

import com.example.kotsuexample.entity.StudyRoomMember;
import com.example.kotsuexample.entity.enums.StudyTag;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
@Builder
public class StudyRoomDetailDto {
    private Integer id;
    private String name;
    private String rule;
    private String notice;
    private String imageUrl;
    private Integer maxUserCount;
    private Integer currentMemberCount;
    private Set<StudyTag> tags;
    private Integer leaderId;
    private LocalDateTime createdAt;
    private List<StudyRoomMemberDto> members;
}
