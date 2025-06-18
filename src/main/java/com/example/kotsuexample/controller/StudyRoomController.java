package com.example.kotsuexample.controller;

import com.example.kotsuexample.config.CurrentUser;
import com.example.kotsuexample.dto.study.*;
import com.example.kotsuexample.service.StudyRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/study-room")
public class StudyRoomController {

    private final StudyRoomService studyRoomService;

    // 1. 스터디룸 생성
    @PostMapping
    public ResponseEntity<StudyRoomDto> createStudyRoom(
            @CurrentUser Integer userId,
            @RequestBody CreateStudyRoomRequest dto
    ) {
        return ResponseEntity.ok(studyRoomService.createStudyRoom(userId, dto));
    }

    // 2. 스터디룸 목록 조회 (인증 없이도 가능)
    @GetMapping
    public ResponseEntity<List<StudyRoomSummaryDto>> getStudyRoomList() {
        return ResponseEntity.ok(studyRoomService.getStudyRoomList());
    }

    // 3. 스터디룸 상세 조회 (인증 없이도 가능)
    @GetMapping("/{id}")
    public ResponseEntity<StudyRoomDetailDto> getStudyRoom(@PathVariable Integer id) {
        return ResponseEntity.ok(studyRoomService.getStudyRoom(id));
    }

    // 4. 스터디룸 정보 수정 (리더만 가능하도록!)
    @PutMapping("/{id}")
    public ResponseEntity<StudyRoomDto> updateStudyRoom(
            @CurrentUser Integer userId,
            @PathVariable Integer id,
            @RequestBody UpdateStudyRoomRequest dto
    ) {
        return ResponseEntity.ok(studyRoomService.updateStudyRoom(userId, id, dto));
    }

    // 5. 스터디룸 삭제 (리더만 가능하도록!)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudyRoom(
            @CurrentUser Integer userId,
            @PathVariable Integer id
    ) {
        studyRoomService.deleteStudyRoom(userId, id);
        return ResponseEntity.noContent().build();
    }
}
