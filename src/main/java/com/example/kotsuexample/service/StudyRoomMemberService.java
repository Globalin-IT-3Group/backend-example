package com.example.kotsuexample.service;

import com.example.kotsuexample.repository.StudyRoomMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StudyRoomMemberService {

    private final StudyRoomMemberRepository studyRoomMemberRepository;


}
