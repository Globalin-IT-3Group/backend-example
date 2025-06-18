package com.example.kotsuexample.repository;

import com.example.kotsuexample.entity.StudyRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyRoomRepository extends JpaRepository<StudyRoom, Integer> {
    boolean existsByName(String name);
}
