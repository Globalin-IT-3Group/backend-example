package com.example.kotsuexample.repository;

import com.globalin.kotsukotsu.entity.StudyNote;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyNoteRepository extends JpaRepository<StudyNote, Integer> {
}
