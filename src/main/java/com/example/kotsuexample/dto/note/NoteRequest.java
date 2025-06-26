package com.example.kotsuexample.dto.note;

import lombok.Getter;

@Getter
public class NoteRequest {
    private String imageUrl;
    private String title;
    private String content;
}
