package com.example.kotsuexample.entity.enums;

public enum ExamType {
    JLPT,
    JPT,
    BASIC;

    public static ExamType from(String value) {
        return ExamType.valueOf(value.trim().toUpperCase());
    }
}
