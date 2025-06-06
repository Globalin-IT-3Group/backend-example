package com.example.kotsuexample.exception;

import com.example.kotsuexample.exception.user.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<ErrorResponseDTO> catchUserNotFoundByPhoneNumberAndAnswer(UserNotFoundByPhoneNumberAndAnswer e) {
        ErrorResponseDTO errorResponseDTO = ErrorResponseDTO.builder()
                .message(e.getMessage())
                .build();

        return new ResponseEntity<>(errorResponseDTO, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponseDTO> catchUserNotFoundByIdException(UserNotFoundByIdException e) {
        ErrorResponseDTO errorResponseDTO = ErrorResponseDTO.builder()
                .message(e.getMessage())
                .build();

        return new ResponseEntity<>(errorResponseDTO, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponseDTO> catchUserNotFoundByEmailException(UserNotFoundByEmailException e) {
        ErrorResponseDTO errorResponseDTO = ErrorResponseDTO.builder()
                .message(e.getMessage())
                .build();

        return new ResponseEntity<>(errorResponseDTO, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponseDTO> catchUserNotFoundByEmailAndAnswer(UserNotFoundByEmailAndAnswer e) {
        ErrorResponseDTO errorResponseDTO = ErrorResponseDTO.builder()
                .message(e.getMessage())
                .build();

        return new ResponseEntity<>(errorResponseDTO, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponseDTO> catchUserLoginException(UserLoginException e) {
        ErrorResponseDTO errorResponseDTO = ErrorResponseDTO.builder()
                .message(e.getMessage())
                .build();

        return new ResponseEntity<>(errorResponseDTO, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponseDTO> catchExistNicknameException(ExistNicknameException e) {
        ErrorResponseDTO errorResponseDTO = ErrorResponseDTO.builder()
                .message(e.getMessage())
                .build();

        return new ResponseEntity<>(errorResponseDTO, HttpStatus.NOT_FOUND);
    }

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
