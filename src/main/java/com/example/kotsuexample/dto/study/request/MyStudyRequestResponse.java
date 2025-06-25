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
public class MyStudyRequestResponse { // 내 요청 내용 보기
    private Integer id;
    private UserResponse leader;
    private String recruitTitle;
    private String recruitExplain;
    private String recruitImage;
    private String requestTitle;
    private String requestMessage;
    private StudyRequestStatus status;
    private LocalDateTime requestedAt;

    public static MyStudyRequestResponse from(StudyRequest entity) {
        StudyRecruit recruit = entity.getStudyRecruit();
        return MyStudyRequestResponse.builder()
                .id(entity.getId())
                .leader(recruit.getLeader().toUserResponse())
                .recruitTitle(recruit.getTitle())
                .recruitExplain(recruit.getStudyExplain())
                .recruitImage(recruit.getStudyRoom().getImageUrl())
                .requestTitle(entity.getTitle())
                .requestMessage(entity.getMessage())
                .status(entity.getStatus())
                .requestedAt(entity.getRequestedAt())
                .build();
    }
}
