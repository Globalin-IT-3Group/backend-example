package com.example.kotsuexample.entity.enums;

public enum EntryType {
    WORD,
    GRAMMAR;

    public static EntryType from(String value) {
        return EntryType.valueOf(value.trim().toUpperCase());
    }
}
