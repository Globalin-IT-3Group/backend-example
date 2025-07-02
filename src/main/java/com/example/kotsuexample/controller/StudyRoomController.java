package com.example.kotsuexample.controller;

import com.example.kotsuexample.config.CurrentUser;
import com.example.kotsuexample.dto.study.*;
import com.example.kotsuexample.service.StudyRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/study-room")
public class StudyRoomController {

    private final StudyRoomService studyRoomService;

    // 1. 스터디룸 생성
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<StudyRoomDto> createStudyRoom(
            @CurrentUser Integer userId,
            @RequestPart("data") CreateStudyRoomRequest dto,
            @RequestPart(value = "image", required = false) MultipartFile imageFile
    ) {
        return ResponseEntity.ok(studyRoomService.createStudyRoom(userId, dto, imageFile));
    }

    // 2. 스터디룸 목록 조회
    @GetMapping
    public ResponseEntity<List<StudyRoomSummaryDto>> getStudyRoomList(@CurrentUser Integer userId) {
        return ResponseEntity.ok(studyRoomService.getStudyRoomList(userId));
    }

    // 3. 스터디룸 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<StudyRoomDetailDto> getStudyRoom(@CurrentUser Integer userId, @PathVariable Integer id) {
        return ResponseEntity.ok(studyRoomService.getStudyRoom(userId, id));
    }

    // 4. 스터디룸 정보 수정 (리더만 가능하도록!)
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<StudyRoomDto> updateStudyRoom(
            @CurrentUser Integer userId,
            @PathVariable Integer id,
            @RequestPart("data") UpdateStudyRoomRequest dto,
            @RequestPart(value = "image", required = false) MultipartFile imageFile
    ) {
        return ResponseEntity.ok(studyRoomService.updateStudyRoom(userId, id, dto, imageFile));
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

    @DeleteMapping("/{id}/leave")
    public ResponseEntity<Void> leaveStudyRoom(
            @CurrentUser Integer userId,
            @PathVariable Integer id
    ) {
        studyRoomService.leaveStudyRoom(userId, id);
        return ResponseEntity.noContent().build();
    }
}
