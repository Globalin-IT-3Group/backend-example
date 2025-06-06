package com.example.kotsuexample.exception.user;

public class UserLoginException extends RuntimeException {
    public UserLoginException(String message) {
        super(message);
    }
}
