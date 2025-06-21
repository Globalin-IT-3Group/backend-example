package com.example.kotsuexample.repository;

import com.example.kotsuexample.entity.StudyRecruit;
import com.example.kotsuexample.entity.StudyRoom;
import com.example.kotsuexample.entity.enums.StudyTag;
import io.lettuce.core.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StudyRecruitRepository extends JpaRepository<StudyRecruit, Integer> {
    Page<StudyRecruit> findByIsOpenFalse(Pageable pageable);

    Page<StudyRecruit> findByTitleContainingAndIsOpenTrue(String title, Pageable pageable);

    boolean existsByStudyRoom(StudyRoom studyRoom);

    // 태그 조건 없이 전체 오픈 구인
    @Query("SELECT sr FROM StudyRecruit sr WHERE sr.isOpen = true")
    Page<StudyRecruit> findAllOpen(Pageable pageable);

    // 태그로 필터 (하나라도 해당 태그가 포함된 스터디)
    @Query("""
    SELECT sr
    FROM StudyRecruit sr
    JOIN sr.studyRoom srRoom
    JOIN srRoom.tags t
    WHERE sr.isOpen = true
      AND t IN :tags
    GROUP BY sr
    HAVING COUNT(DISTINCT t) = :tagCount
    """)
    Page<StudyRecruit> findOpenByTagsAllMatched(@Param("tags") List<StudyTag> tags, @Param("tagCount") long tagCount, Pageable pageable);

    // 제목 검색 (optional, 참고)
    @Query("""
        SELECT sr FROM StudyRecruit sr
        JOIN sr.studyRoom srRoom
        WHERE sr.isOpen = true AND sr.title LIKE %:title%
        """)
    Page<StudyRecruit> searchOpenByTitle(@Param("title") String title, Pageable pageable);
}
