package com.example.kotsuexample.service;

import com.example.kotsuexample.dto.study.StudyNoteCommentCreateDTO;
import com.example.kotsuexample.dto.study.StudyNoteCommentDTO;
import com.example.kotsuexample.dto.study.StudyNoteCommentUpdateDTO;
import com.example.kotsuexample.entity.StudyNoteComment;
import com.example.kotsuexample.repository.StudyNoteCommentRepository;
import com.example.kotsuexample.repository.StudyNoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StudyNoteCommentService {
    private final StudyNoteCommentRepository commentRepository;
    private final UserService userService;
    private final StudyNoteRepository studyNoteRepository;

    public List<StudyNoteCommentDTO> getCommentsByNoteId(Integer noteId) {
        // 댓글 최신순으로 정렬해도 좋음 (createdAt desc)
        var comments = commentRepository.findByStudyNoteIdOrderByCreatedAtAsc(noteId);
        return comments.stream().map(StudyNoteCommentDTO::fromEntity).toList();
    }

    public void createComment(Integer userId, Integer noteId, StudyNoteCommentCreateDTO dto) {
        var user = userService.getUserById(userId);
        var note = studyNoteRepository.findById(noteId)
                .orElseThrow(() -> new IllegalArgumentException("노트 없음"));
        StudyNoteComment parent = null;
        if (dto.getParentCommentId() != null) {
            parent = commentRepository.findById(dto.getParentCommentId())
                    .orElseThrow(() -> new IllegalArgumentException("부모 댓글 없음"));
        }
        var comment = StudyNoteComment.builder()
                .user(user)
                .studyNote(note)
                .content(dto.getContent())
                .isSecret(dto.isSecret())
                .parentComment(parent)
                .createdAt(java.time.LocalDateTime.now())
                .updatedAt(java.time.LocalDateTime.now())
                .build();
        commentRepository.save(comment);
    }

    public void updateComment(Integer userId, Integer commentId, StudyNoteCommentUpdateDTO dto) {
        var comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글 없음"));
        if (!comment.getUser().getId().equals(userId))
            throw new org.springframework.security.access.AccessDeniedException("수정 권한 없음");

        comment.updateContentAndSecretAndUpdatedAt(
                dto.getContent(),
                dto.isSecret(),
                LocalDateTime.now());
    }

    public void deleteComment(Integer userId, Integer commentId) {
        var comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글 없음"));
        if (!comment.getUser().getId().equals(userId))
            throw new org.springframework.security.access.AccessDeniedException("삭제 권한 없음");
        commentRepository.delete(comment);
    }
}
