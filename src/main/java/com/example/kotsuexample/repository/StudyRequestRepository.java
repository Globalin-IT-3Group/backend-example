package com.example.kotsuexample.repository;

import com.example.kotsuexample.entity.StudyRequest;
import com.example.kotsuexample.entity.enums.StudyRequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface StudyRequestRepository extends JpaRepository<StudyRequest, Integer> {
    Optional<StudyRequest> findByUserIdAndStudyRecruitId(Integer userId, Integer id);
    List<StudyRequest> findByUserIdOrderByRequestedAtDesc(Integer userId);
    List<StudyRequest> findByStudyRecruitIdOrderByRequestedAtDesc(Integer studyRecruitId);
    List<StudyRequest> findByRequestedAtBeforeAndStatusIn(LocalDateTime before, List<StudyRequestStatus> statuses);

    Page<StudyRequest> findByStudyRecruitId(Integer studyRecruitId, PageRequest requestedAt);

    Page<StudyRequest> findByUserId(Integer userId, PageRequest requestedAt);
}
