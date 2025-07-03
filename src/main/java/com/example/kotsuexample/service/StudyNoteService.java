package com.example.kotsuexample.service;

import com.example.kotsuexample.config.s3.S3UploadProperties;
import com.example.kotsuexample.dto.study.*;
import com.example.kotsuexample.entity.*;
import com.example.kotsuexample.exception.NoAuthorizationException;
import com.example.kotsuexample.exception.StudyDataNotFoundException;
import com.example.kotsuexample.repository.StudyNoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
    private final S3UploadProperties s3UploadProperties;
    private final S3Uploader s3Uploader;

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
    public void createNote(Integer userId, StudyNoteCreateDTO dto, MultipartFile image) {
        User user = userService.getUserById(userId);
        StudyRoom room = studyRoomService.getStudyRoomEntity(dto.getStudyRoomId());

        String thumbnailUrl;
        if (image == null || image.isEmpty()) {
            // 디폴트 썸네일
            thumbnailUrl = "https://kotsubucket.s3.ap-northeast-2.amazonaws.com/user-uploads-prod/study_note_default.jpg";
        } else {
            String fileName = "study-note-" + userId + "-" + System.currentTimeMillis() + ".jpg";
            String uploadPath = "user-uploads-prod/" + fileName;
            thumbnailUrl = s3Uploader.upload(image, uploadPath);
        }

        StudyNote note = StudyNote.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .thumbnail(thumbnailUrl)
                .studyRoom(room)
                .user(user)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        studyNoteRepository.save(note);
    }

    // 노트 수정
    @Transactional
    public void updateNote(Integer userId, Integer noteId, StudyNoteUpdateDTO dto, MultipartFile image) {
        StudyNote note = studyNoteRepository.findById(noteId)
                .orElseThrow(() -> new StudyDataNotFoundException("노트가 조회되지 않습니다."));
        if (!note.getUser().getId().equals(userId))
            throw new NoAuthorizationException("수정 권한 없습니다");

        String thumbnailUrl;

        if (image != null && !image.isEmpty()) {
            // 새 파일 있으면 업로드
            String fileName = "study-note-" + noteId + "-" + System.currentTimeMillis() + ".jpg";
            String uploadPath = "user-uploads-prod/" + fileName;
            thumbnailUrl = s3Uploader.upload(image, uploadPath);
        } else if (dto.getThumbnail() == null || dto.getThumbnail().isBlank()) {
            // 이미지와 썸네일 경로 모두 없으면 디폴트
            thumbnailUrl = "https://kotsubucket.s3.ap-northeast-2.amazonaws.com/user-uploads-prod/study_note_default.jpg";
        } else {
            // 경로가 전달되면 그것을 사용
            thumbnailUrl = dto.getThumbnail();
        }

        note.updateTitleAndContentAndThumbnailAndUpdatedAt(
                dto.getTitle(),
                dto.getContent(),
                thumbnailUrl,
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
