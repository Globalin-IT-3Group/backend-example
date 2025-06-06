package com.example.kotsuexample.controller;

import com.example.kotsuexample.service.StudyNoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/study-note")
public class StudyNoteController {

    private final StudyNoteService studyNoteService;


}
