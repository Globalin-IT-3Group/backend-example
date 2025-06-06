package com.example.kotsuexample.service;

import com.globalin.kotsukotsu.repository.StudyNoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StudyNoteService {

    private final StudyNoteRepository studyNoteRepository;


}
