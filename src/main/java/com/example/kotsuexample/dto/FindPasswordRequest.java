package com.example.kotsuexample.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class FindPasswordRequest {
    private String email;
    private String question;
    private String answer;
}
