package com.example.kotsuexample.controller;

import com.example.kotsuexample.service.KakaoAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class KakaoAuthController {

    private final KakaoAuthService kakaoAuthService;

    @CrossOrigin(origins = "http://localhost:5173")
    @PostMapping("/kakao/user/auth")
    public ResponseEntity<?> kakaoAuth(@RequestBody Map<String, String> payload) {

        String code = payload.get("code");

        if (code == null || code.isEmpty()) {
            return ResponseEntity.badRequest().body("인가 코드가 필요합니다.");
        }

        Map<String, Object> userInfo = kakaoAuthService.kakaoLogin(code);

        return ResponseEntity.ok(userInfo);
    }
}
