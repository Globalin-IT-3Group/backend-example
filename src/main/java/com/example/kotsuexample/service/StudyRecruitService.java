package com.example.kotsuexample.service;

import com.globalin.kotsukotsu.repository.StudyRecruitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StudyRecruitService {

    private final StudyRecruitRepository studyRecruitRepository;


}
