package com.example.kotsuexample.service;

import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class KakaoAuthService {

    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://kauth.kakao.com")
            .build();

    private final String REST_API_KEY = "1db4d2d646ab02b6069344587fe99581";
    private final String REDIRECT_URI = "http://localhost:5173/kakao/login";

    public Map<String, Object> kakaoLogin(String code) {

        Map<String, String> tokenResponse = webClient.post()
                .uri("/oauth/token")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .bodyValue(buildTokenRequestBody(code))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, String>>() {})
                .block();

        String accessToken = tokenResponse.get("access_token");

        Map<String, Object> userInfo = WebClient.create("https://kapi.kakao.com")
                .get()
                .uri("/v2/user/me")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .block();

        return userInfo;
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
