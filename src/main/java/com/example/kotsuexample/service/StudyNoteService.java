package com.example.kotsuexample.service;

import com.example.kotsuexample.dto.study.*;
import com.example.kotsuexample.entity.*;
import com.example.kotsuexample.repository.StudyNoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StudyNoteService {

    private final StudyNoteRepository studyNoteRepository;
    private final UserService userService;
    private final StudyRoomService studyRoomService;
    private final HeartService heartService;
    private final StudyNoteCommentService commentService;

    // 노트 목록 조회 (스터디방 단위)
    @Transactional(readOnly = true)
    public Page<StudyNoteDTO> getNotes(Integer roomId, Integer userId, Pageable pageable) {
        Page<StudyNote> notes = studyNoteRepository.findByStudyRoomId(roomId, pageable);
        return notes.map(note -> {
            boolean hearted = heartService.isHeartedByUser(note.getId(), userId); // 각 노트별로
            return StudyNoteDTO.fromEntity(note, hearted);
        });
    }

    // 노트 상세 조회 (hearted: 내가 좋아요 눌렀는지)
    @Transactional(readOnly = true)
    public StudyNoteDetailDTO getNote(Integer noteId, Integer userId) {
        StudyNote note = studyNoteRepository.findById(noteId)
                .orElseThrow(() -> new IllegalArgumentException("노트 없음"));
        boolean hearted = heartService.isHeartedByUser(noteId, userId);
        return StudyNoteDetailDTO.fromEntity(note, hearted);
    }

    // 노트 생성
    @Transactional
    public void createNote(Integer userId, StudyNoteCreateDTO dto) {
        User user = userService.getUserById(userId);
        StudyRoom room = studyRoomService.getStudyRoomEntity(dto.getStudyRoomId());

        StudyNote note = StudyNote.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .thumbnail(dto.getThumbnail())
                .studyRoom(room)
                .user(user)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        studyNoteRepository.save(note);
    }

    // 노트 수정
    @Transactional
    public void updateNote(Integer userId, Integer noteId, StudyNoteUpdateDTO dto) {
        StudyNote note = studyNoteRepository.findById(noteId)
                .orElseThrow(() -> new IllegalArgumentException("노트 없음"));
        if (!note.getUser().getId().equals(userId))
            throw new AccessDeniedException("수정 권한 없음");

        note.updateTitleAndContentAndThumbnailAndUpdatedAt(
                dto.getTitle(),
                dto.getContent(),
                dto.getThumbnail(),
                LocalDateTime.now());
    }

    // 노트 삭제
    @Transactional
    public void deleteNote(Integer userId, Integer noteId) {
        StudyNote note = studyNoteRepository.findById(noteId)
                .orElseThrow(() -> new IllegalArgumentException("노트 없음"));
        if (!note.getUser().getId().equals(userId))
            throw new AccessDeniedException("삭제 권한 없음");
        studyNoteRepository.delete(note);
    }

    // 좋아요
    @Transactional
    public void likeNote(Integer userId, Integer noteId) {
        heartService.likeNote(userId, noteId);
    }

    // 좋아요 취소
    @Transactional
    public void unlikeNote(Integer userId, Integer noteId) {
        heartService.unlikeNote(userId, noteId);
    }

    // 댓글 목록
    @Transactional(readOnly = true)
    public List<StudyNoteCommentDTO> getComments(Integer noteId) {
        return commentService.getCommentsByNoteId(noteId);
    }

    // 댓글 등록
    @Transactional
    public void createComment(Integer userId, Integer noteId, StudyNoteCommentCreateDTO dto) {
        commentService.createComment(userId, noteId, dto);
    }

    // 댓글 수정
    @Transactional
    public void updateComment(Integer userId, Integer commentId, StudyNoteCommentUpdateDTO dto) {
        commentService.updateComment(userId, commentId, dto);
    }

    // 댓글 삭제
    @Transactional
    public void deleteComment(Integer userId, Integer commentId) {
        commentService.deleteComment(userId, commentId);
    }
}
