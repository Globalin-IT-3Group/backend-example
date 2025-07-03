package com.example.kotsuexample.controller;

import com.example.kotsuexample.config.CurrentUser;
import com.example.kotsuexample.dto.note.NoteRequest;
import com.example.kotsuexample.dto.note.NoteResponse;
import com.example.kotsuexample.service.NoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/note")
public class NoteController {

    private final NoteService noteService;

    // 노트 목록 조회
    @GetMapping("/")
    public ResponseEntity<List<NoteResponse>> getNotes(@CurrentUser Integer userId) {
        return ResponseEntity.ok(noteService.getNotes(userId));
    }

    // 노트 상세 조회
    @GetMapping("/{noteId}")
    public ResponseEntity<NoteResponse> getNote(@CurrentUser Integer userId, @PathVariable Integer noteId) {
        return ResponseEntity.ok(noteService.getNote(userId, noteId));
    }

    // 노트 작성
    @PostMapping(value = "/", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> createNote(
            @CurrentUser Integer userId,
            @RequestPart("note") NoteRequest request,          // 일반 데이터
            @RequestPart(value = "image", required = false) MultipartFile image // 파일
    ) {
        noteService.createNote(userId, request, image);
        return ResponseEntity.ok().build();
    }


    // 노트 수정
    @PutMapping(value = "/{noteId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> updateNote(
            @CurrentUser Integer userId,
            @PathVariable Integer noteId,
            @RequestPart("note") NoteRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        noteService.updateNote(userId, noteId, request, image);
        return ResponseEntity.ok().build();
    }

    // 노트 삭제
    @DeleteMapping("/{noteId}")
    public ResponseEntity<Void> deleteNote(@CurrentUser Integer userId, @PathVariable Integer noteId) {
        noteService.deleteNote(userId, noteId);
        return ResponseEntity.noContent().build();
    }

    // 내 노트 검색
    @GetMapping("/search")
    public ResponseEntity<List<NoteResponse>> searchNotes(
            @CurrentUser Integer userId,
            @RequestParam("title") String title) {
        return ResponseEntity.ok(noteService.searchMyNotes(userId, title));
    }
}
