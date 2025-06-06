package com.example.kotsuexample.service;

import com.globalin.kotsukotsu.repository.StudyRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StudyRequestService {

    private final StudyRequestRepository studyRequestRepository;


}
