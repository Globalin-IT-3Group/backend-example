package com.example.kotsuexample.controller;

import com.example.kotsuexample.dto.*;
import com.example.kotsuexample.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    // 귀찮아서 DTO 안 만듦
    @GetMapping("/check-email")
    public ResponseEntity<Boolean> checkEmail(@RequestParam String email) {
        Boolean isDuplicate = userService.isEmailDuplicated(email);
        return ResponseEntity.ok(isDuplicate);
    }

    @PostMapping("/join")
    public ResponseEntity<Void> join(@RequestBody JoinRequest joinRequest) {
        userService.join(joinRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        LoginResponse loginResponse = userService.login(loginRequest);
        return ResponseEntity.ok(loginResponse);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserInfoDTO> getUserInfo(@PathVariable Integer userId) {
        UserInfoDTO userInfoDTO = userService.getUserInfo(userId);
        return ResponseEntity.ok(userInfoDTO);
    }

    @PutMapping
    public ResponseEntity<UserInfoDTO> updateUserInfo(@RequestBody UserInfoDTO userInfoDTO) {
        UserInfoDTO updatedUserInfoDTO = userService.updateUserInfo(userInfoDTO);
        return ResponseEntity.ok(updatedUserInfoDTO);
    }

    @PostMapping("/find-email")
    public ResponseEntity<ResponseMessage> getUserEmail(@RequestBody FindEmailRequest findEmailRequest) {
        ResponseMessage responseMessage = userService.getUserEmail(findEmailRequest.getPhoneNumber(), findEmailRequest.getAnswer());
        return ResponseEntity.ok(responseMessage);
    }

    @PostMapping("/find-password")
    public ResponseEntity<ResponseMessage> getUserPassword(@RequestBody FindPasswordRequest findPasswordRequest) {
        ResponseMessage responseMessage = userService.getUserPassword(findPasswordRequest.getEmail(), findPasswordRequest.getAnswer());
        return ResponseEntity.ok(responseMessage);
    }
}
