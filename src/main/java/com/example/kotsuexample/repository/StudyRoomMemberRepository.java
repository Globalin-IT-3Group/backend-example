package com.example.kotsuexample.repository;

import com.example.kotsuexample.entity.StudyRoom;
import com.example.kotsuexample.entity.StudyRoomMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyRoomMemberRepository extends JpaRepository<StudyRoomMember, Integer> {
    int countByStudyRoom(StudyRoom studyRoom);
}
