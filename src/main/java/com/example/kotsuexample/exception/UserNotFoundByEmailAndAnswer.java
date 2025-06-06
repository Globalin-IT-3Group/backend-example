package com.example.kotsuexample.exception;

public class UserNotFoundByEmailAndAnswer extends RuntimeException {
    public UserNotFoundByEmailAndAnswer(String message) {
        super(message);
    }
}
