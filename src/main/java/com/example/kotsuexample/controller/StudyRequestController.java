package com.example.kotsuexample.controller;

import com.example.kotsuexample.service.StudyRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/study-request")
public class StudyRequestController {

    private final StudyRequestService studyRequestService;


}
