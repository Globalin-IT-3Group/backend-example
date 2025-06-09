package com.example.kotsuexample.dto.kakao;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KakaoAccountDTO {
    private String email;
    private KakaoProfileDTO profile;
}
