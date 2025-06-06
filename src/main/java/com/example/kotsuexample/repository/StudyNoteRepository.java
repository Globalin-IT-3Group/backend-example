package com.example.kotsuexample.repository;

import com.example.kotsuexample.entity.StudyNote;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyNoteRepository extends JpaRepository<StudyNote, Integer> {
}
