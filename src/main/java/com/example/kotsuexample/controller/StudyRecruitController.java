package com.example.kotsuexample.controller;

import com.example.kotsuexample.service.StudyRecruitService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/study-recruit")
public class StudyRecruitController {

    private final StudyRecruitService studyRecruitService;


}
