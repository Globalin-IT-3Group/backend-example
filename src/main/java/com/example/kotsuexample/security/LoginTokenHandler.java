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
    private final RedisUtil redisUtil;

    public void issueLoginToken(String userId, HttpServletResponse response) {
        String jwt = jwtTokenProvider.createAccessToken(userId);

        redisUtil.saveAccessToken("LOGIN_" + userId, jwt, JwtTokenProvider.getAccessTokenExpirationTime());

        ResponseCookie cookie = ResponseCookie.from("AUTH_TOKEN", jwt)
                .httpOnly(true)
                .secure(false) // TODO: 운영 환경에서는 true
                .path("/")
                .maxAge(JwtTokenProvider.getAccessTokenExpirationTime())
                .build();

        response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}
