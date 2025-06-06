package com.example.kotsuexample.exception.user;

public class UserNotFoundByEmailAndAnswer extends RuntimeException {
    public UserNotFoundByEmailAndAnswer(String message) {
        super(message);
    }
}
