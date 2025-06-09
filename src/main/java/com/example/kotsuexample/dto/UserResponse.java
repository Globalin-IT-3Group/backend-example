package com.example.kotsuexample.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserResponse {
    private Integer id;
    private String nickname;
    private String profileImage;
    private String profileMessage;

    @Builder
    public UserResponse(Integer id, String nickname, String profileImage, String profileMessage) {
        this.id = id;
        this.nickname = nickname;
        this.profileImage = profileImage;
        this.profileMessage = profileMessage;
    }
}
