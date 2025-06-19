package com.example.kotsuexample.repository;

import com.example.kotsuexample.entity.StudyNote;
import com.example.kotsuexample.entity.StudyNoteComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudyNoteRepository extends JpaRepository<StudyNote, Integer> {
    Page<StudyNote> findByStudyRoomId(Integer roomId, Pageable pageable);
}
