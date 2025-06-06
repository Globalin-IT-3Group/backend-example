package com.example.kotsuexample.controller;

import com.globalin.kotsukotsu.service.StudyRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/study-room")
public class StudyRoomController {

    private final StudyRoomService studyRoomService;


}
