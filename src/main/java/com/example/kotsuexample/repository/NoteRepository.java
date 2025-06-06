package com.example.kotsuexample.repository;

import com.globalin.kotsukotsu.entity.Note;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoteRepository extends JpaRepository<Note, Integer> {
}
