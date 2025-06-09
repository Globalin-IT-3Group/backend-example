package com.example.kotsuexample.dto;

import com.example.kotsuexample.entity.enums.SignupType;
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
    private SignupType signupType;

    @Builder
    public LoginResponse(Integer id, String email, String nickname, String profileImage, SignupType signupType) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
        this.profileImage = profileImage;
        this.signupType = signupType;
    }
}
