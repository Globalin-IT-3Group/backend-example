package com.example.kotsuexample.dto.study;

import com.example.kotsuexample.dto.UserResponse;
import com.example.kotsuexample.entity.StudyRequest;
import com.example.kotsuexample.entity.enums.StudyRequestStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class StudyRequestResponse {
    private Integer id;
    private UserResponse user;
    private Integer studyRecruitId;
    private String studyTitle;
    private String message;
    private StudyRequestStatus status;
    private LocalDateTime requestedAt;

    public static StudyRequestResponse from(StudyRequest entity) {
        return StudyRequestResponse.builder()
                .id(entity.getId())
                .user(UserResponse.builder()
                        .id(entity.getUser().getId())
                        .nickname(entity.getUser().getNickname())
                        .profileImage(entity.getUser().getProfileImage())
                        .profileMessage(entity.getUser().getProfileMessage())
                        .build())
                .studyRecruitId(entity.getStudyRecruit().getId())
                .studyTitle(entity.getStudyRecruit().getTitle())
                .message(entity.getMessage())
                .status(entity.getStatus())
                .requestedAt(entity.getRequestedAt())
                .build();
    }
}
