package com.example.kotsuexample.dto;

import com.example.kotsuexample.entity.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class JoinRequest {

    private String phoneNumber;
    private String email;
    private String password;
    private String nickname;
    private String question;
    private String answer;

    @Builder
    public JoinRequest(String phoneNumber, String email, String password, String nickname, String question, String answer) {
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.question = question;
        this.answer = answer;
    }

    public User toEntity() {
        return User.builder()
                .phoneNumber(this.phoneNumber)
                .email(this.email)
                .password(this.password)
                .nickname(this.nickname)
                .question(this.question)
                .answer(this.answer)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
