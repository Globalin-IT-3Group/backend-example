package com.example.kotsuexample.service;

import com.example.kotsuexample.config.redis.RedisUtil;
import com.example.kotsuexample.dto.LoginResponse;
import com.example.kotsuexample.dto.kakao.KakaoUserInfoDTO;
import com.example.kotsuexample.entity.User;
import com.example.kotsuexample.entity.enums.SignupType;
import com.example.kotsuexample.exception.user.UserNoAccessTokenFromKakaoException;
import com.example.kotsuexample.exception.user.UserNoDataFromKakaoException;
import com.example.kotsuexample.exception.user.UserNotFoundByEmailException;
import com.example.kotsuexample.repository.UserRepository;
import com.example.kotsuexample.security.JwtTokenProvider;
import com.example.kotsuexample.security.LoginTokenHandler;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class KakaoAuthService {

    private final UserRepository userRepository;
    private final LoginTokenHandler loginTokenHandler;
    private final RedisUtil redisUtil;

    @Value("${kakao.rest-api-key}")
    private String REST_API_KEY;

    @Value("${kakao.redirect-uri}")
    private String REDIRECT_URI;

    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://kauth.kakao.com")
            .build();

    public LoginResponse kakaoLogin(String code, HttpServletResponse response) {
        Map<String, String> tokenResponse = webClient.post()
                .uri("/oauth/token")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .bodyValue(buildTokenRequestBody(code))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, String>>() {})
                .block();

        if (tokenResponse == null) {
            throw new UserNoAccessTokenFromKakaoException("카카오로부터 토큰 응답이 오지 않았습니다.");
        }

        String accessToken = tokenResponse.get("access_token");

        KakaoUserInfoDTO userInfo = WebClient.create("https://kapi.kakao.com")
                .get()
                .uri("/v2/user/me")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(KakaoUserInfoDTO.class)
                .block();

        if (userInfo == null || userInfo.getKakao_account() == null) {
            throw new UserNoDataFromKakaoException("카카오로부터 넘어온 유저 데이터가 없습니다.");
        }

        String email = userInfo.getKakao_account().getEmail();
        String nickname = userInfo.getKakao_account().getProfile().getNickname();
        String profileImageUrl = userInfo.getKakao_account().getProfile().getProfile_image_url();

        boolean isExistUser = userRepository.existsByEmail(email);
        LoginResponse loginResponse;

        if (!isExistUser) {
            User user = User.kakaoUser(email, nickname, profileImageUrl, SignupType.KAKAO, LocalDateTime.now());
            User newUser = userRepository.save(user);
            loginResponse = newUser.toLoginResponse();
        } else {
            User existUser = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UserNotFoundByEmailException("이메일에 따른 유저가 조회되지 않습니다."));
            loginResponse = existUser.toLoginResponse();
        }

        // ✅ 여기부터 구조 분리
        String userId = String.valueOf(loginResponse.getId());

        // 1. 토큰 생성
        String jwt = loginTokenHandler.createToken(userId);

        // 2. Redis 저장
        redisUtil.saveAccessToken("LOGIN_" + userId, jwt, JwtTokenProvider.getAccessTokenExpirationTime());

        // 3. 쿠키 생성 및 응답에 설정
        ResponseCookie cookie = loginTokenHandler.createCookie(jwt);
        response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return loginResponse;
    }

    private MultiValueMap<String, String> buildTokenRequestBody(String code) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "authorization_code");
        formData.add("client_id", REST_API_KEY);
        formData.add("redirect_uri", REDIRECT_URI);
        formData.add("code", code);
        return formData;
    }
}

