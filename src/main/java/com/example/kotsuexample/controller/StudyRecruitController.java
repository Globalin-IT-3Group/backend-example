package com.example.kotsuexample.controller;

import com.example.kotsuexample.dto.study.StudyRecruitSaveRequestDTO;
import com.example.kotsuexample.dto.study.StudyRecruitDTO;
import com.example.kotsuexample.entity.enums.StudyTag;
import com.example.kotsuexample.service.StudyRecruitService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/study-recruit")
public class StudyRecruitController {

    private final StudyRecruitService studyRecruitService;

    // 태그 필터 & 페이지네이션
    @GetMapping
    public Page<StudyRecruitDTO> getOpenStudyRecruits(
            @RequestParam(required = false) List<StudyTag> tags,
            Pageable pageable
    ) {
        return studyRecruitService.getOpenStudyRecruits(tags, pageable);
    }

    // 검색 페이지네이션 (title 기준)
    @GetMapping("/search")
    public Page<StudyRecruitDTO> searchStudyRecruit(
            @RequestParam String title,
            Pageable pageable
    ) {
        return studyRecruitService.searchStudyRecruitsByTitle(title, pageable);
    }

    // 모집글 생성
    @PostMapping
    public StudyRecruitDTO createRecruit(@RequestBody StudyRecruitSaveRequestDTO dto) {
        return studyRecruitService.createStudyRecruit(dto);
    }

    // 모집글 수정
    @PutMapping("/{recruitId}")
    public StudyRecruitDTO updateRecruit(
            @PathVariable Integer recruitId,
            @RequestBody StudyRecruitSaveRequestDTO dto
    ) {
        return studyRecruitService.updateStudyRecruit(recruitId, dto);
    }
}
