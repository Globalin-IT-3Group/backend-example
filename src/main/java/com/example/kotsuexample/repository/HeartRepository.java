package com.example.kotsuexample.repository;

import com.example.kotsuexample.entity.Heart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HeartRepository extends JpaRepository<Heart, Integer> {
    boolean existsByStudyNoteIdAndUserId(Integer studyNoteId, Integer userId);
    Optional<Heart> findByStudyNoteIdAndUserId(Integer studyNoteId, Integer userId);
}