package com.example.kotsuexample.exception;

public class UserNotFoundByPhoneNumberAndAnswer extends RuntimeException {
    public UserNotFoundByPhoneNumberAndAnswer(String message) {
        super(message);
    }
}
