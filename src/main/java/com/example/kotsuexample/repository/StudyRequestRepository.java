package com.example.kotsuexample.repository;

import com.globalin.kotsukotsu.entity.StudyRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyRequestRepository extends JpaRepository<StudyRequest, Integer> {
}
