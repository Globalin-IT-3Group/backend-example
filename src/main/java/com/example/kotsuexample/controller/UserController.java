package com.example.kotsuexample.controller;

import com.example.kotsuexample.dto.*;
import com.example.kotsuexample.config.CurrentUser;
import com.example.kotsuexample.exception.user.UserDuplicateException;
import com.example.kotsuexample.exception.user.NoneInputValueException;
import com.example.kotsuexample.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @GetMapping("/check-email")
    public ResponseEntity<ResponseData<Boolean>> checkEmail(@RequestParam String email) {
        Boolean isDuplicate = userService.isEmailDuplicated(email);
        return ResponseEntity.ok(ResponseData.<Boolean>builder()
                .data(isDuplicate)
                .build());
    }

    @PostMapping("/join")
    public ResponseEntity<Void> join(@RequestBody JoinRequest joinRequest) {
        userService.join(joinRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        LoginResponse loginResponse = userService.login(loginRequest, response);
        return ResponseEntity.ok(loginResponse);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserInfoDTO> getUserInfo(@PathVariable Integer userId) {
        UserInfoDTO userInfoDTO = userService.getUserInfo(userId);
        return ResponseEntity.ok(userInfoDTO);
    }

    @PutMapping("/nickname")
    public ResponseEntity<ResponseData<Boolean>> updateNickname(
            @CurrentUser Integer userId, @RequestBody Map<String, String> payload) {
        String inputtedNickname = payload.get("nickname");

        if (inputtedNickname.isEmpty()) throw new NoneInputValueException("입력 값이 없습니다.");

        boolean isDuplicate = userService.isNicknameDuplicated(inputtedNickname);

        if (isDuplicate) throw new UserDuplicateException("존재하는 닉네임입니다.");

        userService.updateNickname(userId, inputtedNickname);

        return ResponseEntity.ok(ResponseData.<Boolean>builder()
                .data(true)
                .build());
    }

    // TODO: 프로필메시지
    @PutMapping("/password")
    public ResponseEntity<ResponseData<Boolean>> updatePassword(
            @CurrentUser Integer userId, @RequestBody Map<String, String> payload) {
        String inputtedPassword = payload.get("password");

        if (inputtedPassword.isEmpty()) throw new NoneInputValueException("입력 값이 없습니다.");

        userService.updatePassword(userId, inputtedPassword);

        return ResponseEntity.ok(ResponseData.<Boolean>builder()
                .data(true)
                .build());
    }

    @PutMapping("/profile-message")
    public ResponseEntity<ResponseData<Boolean>> updateProfileMessage(
            @CurrentUser Integer userId, @RequestBody Map<String, String> payload) {
        String inputtedProfileMessage = payload.get("profileMessage");

        userService.updateProfileMessage(userId, inputtedProfileMessage);

        return ResponseEntity.ok(ResponseData.<Boolean>builder()
                .data(true)
                .build());
    }

    @PutMapping("/question-answer")
    public ResponseEntity<ResponseData<Boolean>> updateQuestion(
            @CurrentUser Integer userId, @RequestBody Map<String, String> payload) {
        String inputtedQuestion = payload.get("question");
        String inputtedAnswer = payload.get("answer");

        if (inputtedQuestion.isEmpty() || inputtedAnswer.isEmpty()) {
            throw new NoneInputValueException("입력 값이 없습니다.");
        }

        userService.updateQuestionAndAnswer(userId, inputtedQuestion, inputtedAnswer);

        return ResponseEntity.ok(ResponseData.<Boolean>builder()
                .data(true)
                .build());
    }

//    @PutMapping
//    public ResponseEntity<UserInfoDTO> updateUserInfo(@RequestBody UserInfoDTO userInfoDTO) {
//        UserInfoDTO updatedUserInfoDTO = userService.updateUserInfo(userInfoDTO);
//        return ResponseEntity.ok(updatedUserInfoDTO);
//    }

    @PostMapping("/find-email")
    public ResponseEntity<ResponseData<String>> getUserEmail(@RequestBody FindEmailRequest findEmailRequest) {
        ResponseData<String> responseData = userService.getUserEmail(findEmailRequest.getPhoneNumber(), findEmailRequest.getAnswer());
        return ResponseEntity.ok(responseData);
    }

    @PostMapping("/find-password")
    public ResponseEntity<ResponseData<String>> getUserPassword(@RequestBody FindPasswordRequest findPasswordRequest) {
        ResponseData<String> responseData = userService.getUserPassword(findPasswordRequest.getEmail(), findPasswordRequest.getAnswer());
        return ResponseEntity.ok(responseData);
    }
}
