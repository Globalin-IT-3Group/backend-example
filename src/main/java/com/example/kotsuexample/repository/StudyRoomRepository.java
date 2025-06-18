package com.example.kotsuexample.repository;

import com.example.kotsuexample.entity.StudyRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StudyRoomRepository extends JpaRepository<StudyRoom, Integer> {
    boolean existsByName(String name);

    @Query("SELECT r FROM StudyRoom r JOIN r.members m WHERE m.user.id = :userId")
    List<StudyRoom> findAllByMemberUserId(@Param("userId") Integer userId);
}
