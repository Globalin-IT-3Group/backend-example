package com.example.kotsuexample.dto.study;

import com.example.kotsuexample.dto.UserResponse;
import com.example.kotsuexample.entity.StudyRecruit;
import com.example.kotsuexample.entity.enums.StudyTag;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class StudyRecruitDTO {
    private Integer id;
    private String imageUrl;
    private String title;
    private String studyExplain;
    private Integer viewCount;
    private UserResponse leader;
    private List<StudyTag> tags;
    private Integer currentMemberCount;
    private Integer maxUserCount;
    private LocalDateTime createdAt;

    @Builder
    public StudyRecruitDTO(
            Integer id, String imageUrl, String title, String studyExplain,
            Integer viewCount, UserResponse leader, List<StudyTag> tags,
            Integer currentMemberCount, Integer maxUserCount, LocalDateTime createdAt
    ) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.title = title;
        this.studyExplain = studyExplain;
        this.viewCount = viewCount;
        this.leader = leader;
        this.tags = tags;
        this.currentMemberCount = currentMemberCount;
        this.maxUserCount = maxUserCount;
        this.createdAt = createdAt;
    }

    public static StudyRecruitDTO fromEntity(StudyRecruit recruit) {
        return StudyRecruitDTO.builder()
                .id(recruit.getId())
                .imageUrl(recruit.getStudyRoom().getImageUrl())
                .title(recruit.getTitle())
                .studyExplain(recruit.getStudyExplain())
                .viewCount(recruit.getViewCount())
                .leader(recruit.getStudyRoom().getLeader().toUserResponse())
                .tags(new ArrayList<>(recruit.getStudyRoom().getTags()))
                .currentMemberCount(recruit.getStudyRoom().getMembers().size())
                .maxUserCount(recruit.getStudyRoom().getMaxUserCount())
                .createdAt(recruit.getCreatedAt())
                .build();
    }
}
