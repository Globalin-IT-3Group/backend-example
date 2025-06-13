package com.example.kotsuexample.entity.enums;

public enum Level {
    N1,
    N2,
    N3,
    N4,
    N5,
    BASIC;

    public static Level from(String value) {
        return Level.valueOf(value.trim().toUpperCase());
    }
}
