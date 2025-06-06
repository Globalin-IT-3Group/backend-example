package com.example.kotsuexample.controller;

import com.globalin.kotsukotsu.service.StudyRoomMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/study-room-member")
public class StudyRoomMemberController {

    private final StudyRoomMemberService studyRoomMemberService;


}
