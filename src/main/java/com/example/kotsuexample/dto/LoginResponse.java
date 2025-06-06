package com.example.kotsuexample.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoginResponse {

    private Integer id;
    private String email;
    private String nickname;
    private String profileImage;

    @Builder
    public LoginResponse(Integer id, String email, String nickname, String profileImage) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
        this.profileImage = profileImage;
    }
}
