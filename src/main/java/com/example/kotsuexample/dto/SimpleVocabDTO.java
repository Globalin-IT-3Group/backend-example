package com.example.kotsuexample.dto;

import com.example.kotsuexample.entity.enums.EntryType;
import com.example.kotsuexample.entity.enums.ExamType;
import com.example.kotsuexample.entity.enums.Level;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SimpleVocabDTO {
    private EntryType entryType;
    private Level level;
    private ExamType examType;
    private String jpWord;
    private String hiragana;
    private String altForm;
    private String pos;
    private String meaning;
}
