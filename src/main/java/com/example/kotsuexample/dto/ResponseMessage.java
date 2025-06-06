package com.example.kotsuexample.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ResponseMessage {

    private String message;

    @Builder
    public ResponseMessage(String message) {
        this.message = message;
    }
}
