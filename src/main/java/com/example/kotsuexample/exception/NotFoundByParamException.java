package com.example.kotsuexample.exception;

public class NotFoundByParamException extends RuntimeException {
    public NotFoundByParamException(String message) {
        super(message);
    }
}
