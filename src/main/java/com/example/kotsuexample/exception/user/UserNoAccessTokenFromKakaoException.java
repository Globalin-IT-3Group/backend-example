package com.example.kotsuexample.exception.user;

public class UserNoAccessTokenFromKakaoException extends RuntimeException {
    public UserNoAccessTokenFromKakaoException(String message) {
        super(message);
    }
}
