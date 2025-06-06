package com.example.kotsuexample.exception;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    @Getter
    @NoArgsConstructor
    private static class ErrorResponseDTO {
        private LocalDateTime timestamp;
        private String message;

        @Builder
        public ErrorResponseDTO(String message) {
            this.timestamp = LocalDateTime.now();
            this.message = message;
        }
    }
}
