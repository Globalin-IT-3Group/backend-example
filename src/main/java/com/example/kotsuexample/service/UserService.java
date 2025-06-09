package com.example.kotsuexample.service;

import com.example.kotsuexample.dto.*;
import com.example.kotsuexample.entity.User;
import com.example.kotsuexample.exception.user.*;
import com.example.kotsuexample.repository.UserRepository;
import com.example.kotsuexample.security.LoginTokenHandler;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final LoginTokenHandler loginTokenHandler;

    public Boolean isEmailDuplicated(String email) {
        return userRepository.existsByEmail(email);
    }

    public void join(JoinRequest joinRequest) {
        String nickname = joinRequest.getNickname();

        boolean isExistNickname = userRepository.existsByNickname(nickname);

        if (isExistNickname) {
            throw new ExistNicknameException("존재하는 닉네임입니다!");
        }

        User newUser = joinRequest.toEntity();
        userRepository.save(newUser);
    }

    public LoginResponse login(LoginRequest loginRequest, HttpServletResponse response) {
        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();

        User foundedUser = userRepository.findByEmailAndPassword(email, password)
                .orElseThrow(() -> new UserLoginException("이메일 또는 비밀번호가 잘못되었습니다!"));

        String userId = String.valueOf(foundedUser.getId());

        loginTokenHandler.issueLoginToken(userId, response);

        return LoginResponse.builder()
                .id(foundedUser.getId())
                .email(foundedUser.getEmail())
                .nickname(foundedUser.getNickname())
                .profileImage(foundedUser.getProfileImage())
                .signupType(foundedUser.getSignupType())
                .build();
    }

    public UserInfoDTO getUserInfo(Integer userId) {

        User foundedUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundByIdException("아이디 값에 따른 유저가 조회되지 않습니다."));

        return UserInfoDTO.builder()
                .phoneNumber(foundedUser.getPhoneNumber())
                .email(foundedUser.getEmail())
                .password(foundedUser.getPassword())
                .nickname(foundedUser.getNickname())
                .question(foundedUser.getQuestion())
                .answer(foundedUser.getAnswer())
                .profileImage(foundedUser.getProfileImage())
                .profileMessage(foundedUser.getProfileMessage())
                .signupType(foundedUser.getSignupType())
                .createdAt(foundedUser.getCreatedAt())
                .build();
    }

    public UserInfoDTO updateUserInfo(UserInfoDTO userInfoDTO) {
        String email = userInfoDTO.getEmail();

        User foundedUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundByEmailException("이메일에 따른 유저가 조회되지 않습니다."));

        User updatedUser = userInfoDTO.toUpdatedEntity(foundedUser.getId());
        userRepository.save(updatedUser);

        return userInfoDTO;
    }

    public ResponseMessage getUserEmail(String phoneNumber, String answer) {
        User foundedUser = userRepository
                .findByPhoneNumberAndAnswer(phoneNumber, answer)
                .orElseThrow(() -> new UserNotFoundByPhoneNumberAndAnswer("전화번호 또는 답변이 잘못되었습니다."));

        String email = foundedUser.getEmail();

        return ResponseMessage.builder()
                .message(email)
                .build();
    }

    public ResponseMessage getUserPassword(String email, String answer) {
        User foundedUser = userRepository
                .findByEmailAndAnswer(email, answer)
                .orElseThrow(() -> new UserNotFoundByEmailAndAnswer("이메일 또는 답변이 잘못되었습니다."));

        String password = foundedUser.getPassword();

        return ResponseMessage.builder()
                .message(password)
                .build();
    }
}
