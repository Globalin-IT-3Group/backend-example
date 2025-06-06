package com.example.kotsuexample.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FindEmailRequest {
    private String phoneNumber;
    private String answer;
}
