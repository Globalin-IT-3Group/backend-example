package com.example.kotsuexample.controller;

import com.example.kotsuexample.config.CurrentUser;
import com.example.kotsuexample.dto.study.*;
import com.example.kotsuexample.service.StudyNoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/study-note")
public class StudyNoteController {

    private final StudyNoteService studyNoteService;

    // 노트 목록
    @GetMapping("/rooms/{roomId}")
    public Page<StudyNoteDTO> getNotes(
            @PathVariable Integer roomId,
            @CurrentUser Integer userId,
            Pageable pageable
    ) {
        return studyNoteService.getNotes(roomId, userId, pageable);
    }

    // 노트 상세
    @GetMapping("/{noteId}")
    public StudyNoteDetailDTO getNote(@CurrentUser Integer userId, @PathVariable Integer noteId) {
        return studyNoteService.getNote(noteId, userId);
    }

    // 노트 생성
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void createNote(
            @CurrentUser Integer userId,
            @RequestPart("note") StudyNoteCreateDTO dto,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        studyNoteService.createNote(userId, dto, image);
    }

    // 노트 수정
    @PutMapping(value = "/{noteId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void updateNote(
            @CurrentUser Integer userId,
            @PathVariable Integer noteId,
            @RequestPart("note") StudyNoteUpdateDTO dto,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        studyNoteService.updateNote(userId, noteId, dto, image);
    }

    // 노트 삭제
    @DeleteMapping("/{noteId}")
    public void deleteNote(@CurrentUser Integer userId, @PathVariable Integer noteId) {
        studyNoteService.deleteNote(userId, noteId);
    }

    // 좋아요 추가
    @PostMapping("/{noteId}/hearts")
    public void likeNote(@CurrentUser Integer userId, @PathVariable Integer noteId) {
        studyNoteService.likeNote(userId, noteId);
    }

    // 좋아요 취소
    @DeleteMapping("/{noteId}/hearts")
    public void unlikeNote(@CurrentUser Integer userId, @PathVariable Integer noteId) {
        studyNoteService.unlikeNote(userId, noteId);
    }

    // 댓글 목록
    @GetMapping("/{noteId}/comments")
    public List<StudyNoteCommentDTO> getComments(@PathVariable Integer noteId) {
        return studyNoteService.getComments(noteId);
    }

    // 댓글 작성
    @PostMapping("/{noteId}/comments")
    public void createComment(@CurrentUser Integer userId, @PathVariable Integer noteId, @RequestBody StudyNoteCommentCreateDTO dto) {
        studyNoteService.createComment(userId, noteId, dto);
    }

    // 댓글 수정
    @PutMapping("/comments/{commentId}")
    public void updateComment(@CurrentUser Integer userId, @PathVariable Integer commentId, @RequestBody StudyNoteCommentUpdateDTO dto) {
        studyNoteService.updateComment(userId, commentId, dto);
    }

    // 댓글 삭제
    @DeleteMapping("/comments/{commentId}")
    public void deleteComment(@CurrentUser Integer userId, @PathVariable Integer commentId) {
        studyNoteService.deleteComment(userId, commentId);
    }
}
