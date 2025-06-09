package com.example.kotsuexample.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ResponseMessage<T> {

    private T message;

    @Builder
    public ResponseMessage(T message) {
        this.message = message;
    }
}
