package com.example.kotsuexample.controller;

import com.example.kotsuexample.dto.study.StudyRecruitSaveRequestDTO;
import com.example.kotsuexample.dto.study.StudyRecruitDTO;
import com.example.kotsuexample.entity.enums.StudyTag;
import com.example.kotsuexample.service.StudyRecruitService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
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

    // 스터디 방에서 모집글 조회
    @GetMapping("/{studyRoomId}")
    public ResponseEntity<StudyRecruitSaveRequestDTO> getStudyRecruitInStudyRoom(
            @PathVariable Integer studyRoomId) {

        StudyRecruitSaveRequestDTO response = studyRecruitService.getStudyRecruitInStudyRoom(studyRoomId);
        return ResponseEntity.ok(response);
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

    // 모집글 viewCount 증가
    @PatchMapping("/{recruitId}/view")
    public ResponseEntity<Void> increaseViewCount(@PathVariable Integer recruitId) {
        studyRecruitService.increaseViewCount(recruitId);
        return ResponseEntity.ok().build();
    }
}
