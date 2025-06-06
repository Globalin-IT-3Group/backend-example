package com.example.kotsuexample.exception.user;

public class ExistNicknameException extends RuntimeException {
    public ExistNicknameException(String message) {
        super(message);
    }
}
