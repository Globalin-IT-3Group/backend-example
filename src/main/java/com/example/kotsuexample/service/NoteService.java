package com.example.kotsuexample.service;

import com.example.kotsuexample.config.s3.S3UploadProperties;
import com.example.kotsuexample.dto.note.NoteRequest;
import com.example.kotsuexample.dto.note.NoteResponse;
import com.example.kotsuexample.entity.Note;
import com.example.kotsuexample.repository.NoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NoteService {

    private final NoteRepository noteRepository;
    private final S3Uploader s3Uploader;
    private final S3UploadProperties s3UploadProperties;

    public List<NoteResponse> getNotes(Integer userId) {
        return noteRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(note -> NoteResponse.builder()
                        .id(note.getId())
                        .imageUrl(note.getImageUrl())
                        .title(note.getTitle())
                        .content(note.getContent())
                        .createdAt(note.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    public NoteResponse getNote(Integer userId, Integer noteId) {
        Note note = noteRepository.findById(noteId)
                .filter(n -> n.getUserId().equals(userId))
                .orElseThrow(() -> new RuntimeException("노트를 찾을 수 없습니다."));
        return NoteResponse.builder()
                .id(note.getId())
                .imageUrl(note.getImageUrl())
                .title(note.getTitle())
                .content(note.getContent())
                .createdAt(note.getCreatedAt())
                .build();
    }

    public void createNote(Integer userId, NoteRequest request, MultipartFile image) {
        String imageUrl;

        if (image != null && !image.isEmpty()) {
            String fileName = "note-" + userId + "-" + UUID.randomUUID() + ".jpg";
            String uploadPath = s3UploadProperties.getUploadDir() + fileName;
            imageUrl = s3Uploader.upload(image, uploadPath);
        } else {
            imageUrl = "https://kotsubucket.s3.ap-northeast-2.amazonaws.com/user-uploads-prod/note_default.jpg";
        }

        Note note = Note.builder()
                .userId(userId)
                .imageUrl(imageUrl)
                .title(request.getTitle())
                .content(request.getContent())
                .createdAt(LocalDateTime.now())
                .build();
        noteRepository.save(note);
    }

    public void updateNote(Integer userId, Integer noteId, NoteRequest request, MultipartFile image) {
        Note note = noteRepository.findById(noteId)
                .filter(n -> n.getUserId().equals(userId))
                .orElseThrow(() -> new RuntimeException("수정할 노트를 찾을 수 없습니다."));

        String imageUrl = note.getImageUrl();

        if (image != null && !image.isEmpty()) {
            String fileName = "note-" + note.getId() + "-" + UUID.randomUUID() + ".jpg";
            String uploadPath = s3UploadProperties.getUploadDir() + fileName;
            imageUrl = s3Uploader.upload(image, uploadPath);
            note.changeImageUrl(imageUrl);
        }

        note.changeTitle(request.getTitle());
        note.changeContent(request.getContent());
        noteRepository.save(note);
    }

    public void deleteNote(Integer userId, Integer noteId) {
        Note note = noteRepository.findById(noteId)
                .filter(n -> n.getUserId().equals(userId))
                .orElseThrow(() -> new RuntimeException("삭제할 노트를 찾을 수 없습니다."));
        noteRepository.delete(note);
    }

    public List<NoteResponse> searchMyNotes(Integer userId, String title) {
        return noteRepository
                .findByUserIdAndTitleContainingIgnoreCaseOrderByCreatedAtDesc(userId, title)
                .stream()
                .map(NoteResponse::fromEntity)
                .toList();
    }
}
