package com.example.kotsuexample.repository;

import com.example.kotsuexample.entity.StudyNoteComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudyNoteCommentRepository extends JpaRepository<StudyNoteComment, Integer> {
    List<StudyNoteComment> findByStudyNoteIdOrderByCreatedAtAsc(Integer noteId);
}
