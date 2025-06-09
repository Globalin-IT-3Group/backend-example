package com.example.kotsuexample.exception.user;

public class UserNoAuthorizationCodeFromKakaoException extends RuntimeException {
    public UserNoAuthorizationCodeFromKakaoException(String message) {
        super(message);
    }
}
