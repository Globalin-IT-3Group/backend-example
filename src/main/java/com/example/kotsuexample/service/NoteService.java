package com.example.kotsuexample.service;

import com.example.kotsuexample.dto.note.NoteRequest;
import com.example.kotsuexample.dto.note.NoteResponse;
import com.example.kotsuexample.entity.Note;
import com.example.kotsuexample.repository.NoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NoteService {

    private final NoteRepository noteRepository;

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

    public void createNote(Integer userId, NoteRequest request) {
        Note note = Note.builder()
                .userId(userId)
                .imageUrl(request.getImageUrl())
                .title(request.getTitle())
                .content(request.getContent())
                .createdAt(LocalDateTime.now())
                .build();
        noteRepository.save(note);
    }

    public void updateNote(Integer userId, Integer noteId, NoteRequest request) {
        Note note = noteRepository.findById(noteId)
                .filter(n -> n.getUserId().equals(userId))
                .orElseThrow(() -> new RuntimeException("수정할 노트를 찾을 수 없습니다."));
        note.changeImageUrl(request.getImageUrl());
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
}
