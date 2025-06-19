package com.example.kotsuexample.dto.study;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StudyRoomMemberDto {
    private Integer userId;
    private String nickname;
    private String profileImageUrl;
    private Boolean isLeader;
}
