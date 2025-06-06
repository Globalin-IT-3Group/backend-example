package com.example.kotsuexample.exception.user;

public class UserNotFoundByPhoneNumberAndAnswer extends RuntimeException {
    public UserNotFoundByPhoneNumberAndAnswer(String message) {
        super(message);
    }
}
