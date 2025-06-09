package com.example.kotsuexample.entity;

import com.example.kotsuexample.dto.LoginResponse;
import com.example.kotsuexample.entity.enums.SignupType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Table(name = "users")
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // 이메일 조회 때 쓸 전화 번호 컬럼 추가
    @Column(name = "phone_number", nullable = false, unique = true)
    private String phoneNumber;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    // 닉네임은 중복을 허용하지 않도록!
    @Column(unique = true)
    private String nickname;

    private String question;

    private String answer;

    @Column(name = "profile_image")
    private String profileImage;

    @Column(name = "profile_message")
    private String profileMessage;

    @Enumerated(EnumType.STRING)
    @Column(name = "signup_type")
    private SignupType signupType;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

//    @JsonIgnore
//    @OneToMany(mappedBy = "leader", cascade = CascadeType.ALL)
//    private List<StudyRoom> studyRooms = new ArrayList<>();

    @Builder
    public User(String phoneNumber, String email, String password, String nickname,
                String question, String answer, String profileImage, String profileMessage,
                SignupType signupType, LocalDateTime createdAt) {
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

    // 카카오 회원 생성용 팩토리 메서드
    public static User kakaoUser(String email, String nickname, String profileImage, SignupType signupType, LocalDateTime createdAt) {
        return User.builder()
                .phoneNumber("KAKAO")
                .password("KAKAO")
                .email(email)
                .nickname(nickname)
                .profileImage(profileImage)
                .signupType(signupType)
                .createdAt(createdAt)
                .build();
    }

    public LoginResponse toLoginResponse() {
        return LoginResponse.builder()
                .id(this.id)
                .email(this.email)
                .profileImage(this.profileImage)
                .nickname(this.nickname)
                .signupType(this.signupType)
                .build();
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void updateProfileMessage(String profileMessage) {
        this.nickname = profileMessage;
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public void updateProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }
}
