package com.example.kotsuexample.controller;

import com.example.kotsuexample.dto.LoginResponse;
import com.example.kotsuexample.exception.user.UserNoAuthorizationCodeFromKakaoException;
import com.example.kotsuexample.service.KakaoAuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class KakaoAuthController {

    private final KakaoAuthService kakaoAuthService;

    @PostMapping("/kakao/user/auth")
    public ResponseEntity<?> kakaoAuth(@RequestBody Map<String, String> payload, HttpServletResponse response) {

        String code = payload.get("code");

        if (code == null || code.isEmpty()) {
            throw new UserNoAuthorizationCodeFromKakaoException("인가 코드가 필요합니다.");
        }

        LoginResponse loginResponse = kakaoAuthService.kakaoLogin(code, response);

        return ResponseEntity.ok(loginResponse);
    }
}
