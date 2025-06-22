package com.example.kotsuexample.dto.study;

import com.example.kotsuexample.dto.UserResponse;
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
}
