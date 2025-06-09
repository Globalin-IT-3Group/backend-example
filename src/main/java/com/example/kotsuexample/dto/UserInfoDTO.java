package com.example.kotsuexample.dto;

import com.example.kotsuexample.entity.User;
import com.example.kotsuexample.entity.enums.SignupType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class UserInfoDTO {

    private String phoneNumber;
    private String email;
    private String password;
    private String nickname;
    private String question;
    private String answer;
    private String profileImage;
    private String profileMessage;
    private SignupType signupType;
    private LocalDateTime createdAt;

    @Builder
    public UserInfoDTO(String phoneNumber, String email, String password, String nickname, String question, String answer, String profileImage, String profileMessage, SignupType signupType, LocalDateTime createdAt) {
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.question = question;
        this.answer = answer;
        this.profileImage = profileImage;
        this.profileMessage = profileMessage;
        this.signupType = signupType;
        this.createdAt = createdAt;
    }

    public User toUpdatedEntity(Integer userId) {
        return User.builder()
                .phoneNumber(this.phoneNumber)
                .email(this.email)
                .password(this.password)
                .nickname(this.nickname)
                .question(this.question)
                .answer(this.answer)
                .profileImage(this.profileImage)
                .profileMessage(this.profileMessage)
                .signupType(this.signupType)
                .createdAt(this.createdAt)
                .build();
    }
}
