package com.example.kotsuexample.repository;

import com.example.kotsuexample.entity.Note;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoteRepository extends JpaRepository<Note, Integer> {
    List<Note> findByUserIdOrderByCreatedAtDesc(Integer userId);
}
