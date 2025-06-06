package com.example.kotsuexample.repository;

import com.example.kotsuexample.entity.Note;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoteRepository extends JpaRepository<Note, Integer> {
}
