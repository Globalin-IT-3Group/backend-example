package com.example.kotsuexample.service;

import com.example.kotsuexample.config.redis.RedisUtil;
import com.example.kotsuexample.dto.*;
import com.example.kotsuexample.entity.User;
import com.example.kotsuexample.exception.user.*;
import com.example.kotsuexample.repository.UserRepository;
import com.example.kotsuexample.security.JwtTokenProvider;
import com.example.kotsuexample.security.LoginTokenHandler;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final LoginTokenHandler loginTokenHandler;
    private final RedisUtil redisUtil;

    public Boolean isEmailDuplicated(String email) {
        return userRepository.existsByEmail(email);
    }

    public void join(JoinRequest joinRequest) {
        String email = joinRequest.getEmail();
        String nickname = joinRequest.getNickname();

        boolean isExistEmail = userRepository.existsByEmail(email);
        if (isExistEmail) throw new DuplicateException("존재하는 이메일입니다!");

        boolean isExistNickname = userRepository.existsByNickname(nickname);
        if (isExistNickname) throw new DuplicateException("존재하는 닉네임입니다!");

        User newUser = joinRequest.toEntity();
        userRepository.save(newUser);
    }

    public LoginResponse login(LoginRequest loginRequest, HttpServletResponse response) {
        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();

        User foundedUser = userRepository.findByEmailAndPassword(email, password)
                .orElseThrow(() -> new UserLoginException("이메일 또는 비밀번호가 잘못되었습니다!"));

        String userId = String.valueOf(foundedUser.getId());

        // 토큰 발급
        String jwt = loginTokenHandler.createToken(userId);

        // Redis에 토큰 저장
        redisUtil.saveAccessToken("LOGIN_" + userId, jwt, JwtTokenProvider.getAccessTokenExpirationTime());

        // 쿠키 발급
        ResponseCookie cookie = loginTokenHandler.createCookie(jwt);
        response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());

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

    public ResponseData<String> getUserEmail(FindEmailRequest findEmailRequest) {
        String phoneNumber = findEmailRequest.getPhoneNumber();
        String question = findEmailRequest.getQuestion();
        String answer = findEmailRequest.getAnswer();

        User foundedUser = userRepository
                .findByPhoneNumberAndQuestionAndAnswer(phoneNumber, question, answer)
                .orElseThrow(() -> new UserNotFoundByPhoneNumberAndAnswer("전화번호 또는 질문, 답변이 잘못되었습니다."));

        String email = foundedUser.getEmail();

        return ResponseData.<String>builder()
                .data(email)
                .build();
    }

    public ResponseData<String> getUserPassword(FindPasswordRequest findPasswordRequest) {
        String email = findPasswordRequest.getEmail();
        String question = findPasswordRequest.getQuestion();
        String answer = findPasswordRequest.getAnswer();

        User foundedUser = userRepository
                .findByEmailAndQuestionAndAnswer(email, question, answer)
                .orElseThrow(() -> new UserNotFoundByEmailAndAnswer("이메일 또는 질문, 답변이 잘못되었습니다."));

        String password = foundedUser.getPassword();

        return ResponseData.<String>builder()
                .data(password)
                .build();
    }

    public boolean isNicknameDuplicated(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    public void updateNickname(Integer userId, String inputtedNickname) {
        User foundedUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundByIdException("아이디 값에 따른 유저가 조회되지 않습니다."));

        foundedUser.updateNickname(inputtedNickname);
        userRepository.save(foundedUser);
    }

    public void updatePassword(Integer userId, String inputtedPassword) {
        User foundedUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundByIdException("아이디 값에 따른 유저가 조회되지 않습니다."));

        String currentPassword = foundedUser.getPassword();
        if (currentPassword.equals(inputtedPassword)) throw new SameValueException("현재 비밀번호와 일치합니다.");

        foundedUser.updatePassword(inputtedPassword);
        userRepository.save(foundedUser);
    }

    public void updateProfileMessage(Integer userId, String inputtedProfileMessage) {
        User foundedUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundByIdException("아이디 값에 따른 유저가 조회되지 않습니다."));

        foundedUser.updateProfileMessage(inputtedProfileMessage);
        userRepository.save(foundedUser);
    }

    public void updateQuestionAndAnswer(Integer userId, String inputtedQuestion, String inputtedAnswer) {
        User foundedUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundByIdException("아이디 값에 따른 유저가 조회되지 않습니다."));

        foundedUser.updateQuestionAndAnswer(inputtedQuestion, inputtedAnswer);
        userRepository.save(foundedUser);
    }

    public UserResponse getSimpleUserInfoById(Integer friendId) {
        User user = userRepository.findById(friendId)
                .orElseThrow(() -> new UserNotFoundByIdException("아이디 값에 따른 유저가 조회되지 않습니다."));

        return user.toUserResponse();
    }

    public String updateProfileImage(Integer userId, String profileImage) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundByIdException("아이디 값에 따른 유저가 조회되지 않습니다."));
        user.updateProfileImage(profileImage); // TEXT, CLOB 컬럼
        userRepository.save(user);
        return profileImage;
    }

    public void validExistUser(Integer userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundByIdException("아이디 값에 따른 유저가 조회되지 않습니다."));
    }

    public User getUserById(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundByIdException("아이디 값에 따른 유저가 조회되지 않습니다."));
    }
}
