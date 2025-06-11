package com.example.kotsuexample.security;

import com.example.kotsuexample.config.redis.RedisUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LoginTokenHandler {

    private final JwtTokenProvider jwtTokenProvider;

    public String createToken(String userId) {
        return jwtTokenProvider.createAccessToken(userId);
    }

    public ResponseCookie createCookie(String jwt) {
        return ResponseCookie.from("AUTH_TOKEN", jwt)
                .httpOnly(true)
                .secure(false) // 운영 시 true
                .path("/")
                .maxAge(JwtTokenProvider.getAccessTokenExpirationTime())
                .build();
    }
}
