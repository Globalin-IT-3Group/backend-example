package com.example.kotsuexample.exception.user;

public class UserNotFoundByEmailException extends RuntimeException {
    public UserNotFoundByEmailException(String message) {
        super(message);
    }
}
