package com.example.kotsuexample.service;

import com.example.kotsuexample.entity.Heart;
import com.example.kotsuexample.repository.HeartRepository;
import com.example.kotsuexample.repository.StudyNoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HeartService {
    private final HeartRepository heartRepository;
    private final UserService userService;
    private final StudyNoteRepository studyNoteRepository;

    public boolean isHeartedByUser(Integer noteId, Integer userId) {
        return heartRepository.existsByStudyNoteIdAndUserId(noteId, userId);
    }

    public void likeNote(Integer userId, Integer noteId) {
        if (heartRepository.existsByStudyNoteIdAndUserId(noteId, userId))
            return; // 이미 좋아요 상태면 무시

        var user = userService.getUserById(userId);
        var note = studyNoteRepository.findById(noteId)
                .orElseThrow(() -> new IllegalArgumentException("노트 없음"));
        var heart = Heart.builder()
                .user(user)
                .studyNote(note)
                .build();
        heartRepository.save(heart);
    }

    public void unlikeNote(Integer userId, Integer noteId) {
        var heartOpt = heartRepository.findByStudyNoteIdAndUserId(noteId, userId);
        heartOpt.ifPresent(heartRepository::delete);
    }
}
