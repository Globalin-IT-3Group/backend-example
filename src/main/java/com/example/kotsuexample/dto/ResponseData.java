package com.example.kotsuexample.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ResponseData<T> {

    private T data;

    @Builder
    public ResponseData(T data) {
        this.data = data;
    }
}
