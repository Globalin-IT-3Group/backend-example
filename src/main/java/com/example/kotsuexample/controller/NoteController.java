package com.example.kotsuexample.controller;

import com.example.kotsuexample.config.CurrentUser;
import com.example.kotsuexample.dto.note.NoteRequest;
import com.example.kotsuexample.dto.note.NoteResponse;
import com.example.kotsuexample.service.NoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    @PostMapping("/")
    public ResponseEntity<Void> createNote(@CurrentUser Integer userId, @RequestBody NoteRequest request) {
        noteService.createNote(userId, request);
        return ResponseEntity.ok().build();
    }

    // 노트 수정
    @PutMapping("/{noteId}")
    public ResponseEntity<Void> updateNote(@CurrentUser Integer userId,
                                           @PathVariable Integer noteId,
                                           @RequestBody NoteRequest request) {
        noteService.updateNote(userId, noteId, request);
        return ResponseEntity.ok().build();
    }

    // 노트 삭제
    @DeleteMapping("/{noteId}")
    public ResponseEntity<Void> deleteNote(@CurrentUser Integer userId, @PathVariable Integer noteId) {
        noteService.deleteNote(userId, noteId);
        return ResponseEntity.noContent().build();
    }
}
