package com.example.kotsuexample.dto.study.request;

import com.example.kotsuexample.dto.UserResponse;
import com.example.kotsuexample.entity.StudyRecruit;
import com.example.kotsuexample.entity.StudyRequest;
import com.example.kotsuexample.entity.enums.StudyRequestStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class StudyRequestResponse { // 요청자 목록 보기
    private Integer id;
    private UserResponse requester;
    private String requestTitle;
    private String message;
    private StudyRequestStatus status;
    private LocalDateTime requestedAt;

    public static StudyRequestResponse from(StudyRequest studyRequest) {
        return StudyRequestResponse.builder()
                .id(studyRequest.getId())
                .requester(UserResponse.builder()
                        .id(studyRequest.getUser().getId())
                        .nickname(studyRequest.getUser().getNickname())
                        .profileImage(studyRequest.getUser().getProfileImage())
                        .profileMessage(studyRequest.getUser().getProfileMessage())
                        .build())
                .requestTitle(studyRequest.getTitle())
                .message(studyRequest.getMessage())
                .status(studyRequest.getStatus())
                .requestedAt(studyRequest.getRequestedAt())
                .build();
    }
}
