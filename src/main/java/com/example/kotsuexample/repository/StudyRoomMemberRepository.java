package com.example.kotsuexample.repository;

import com.example.kotsuexample.entity.StudyRoom;
import com.example.kotsuexample.entity.StudyRoomMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudyRoomMemberRepository extends JpaRepository<StudyRoomMember, Integer> {
    int countByStudyRoom(StudyRoom studyRoom);

    boolean existsByStudyRoomAndUserId(StudyRoom room, Integer userId);

    Optional<StudyRoomMember> findByStudyRoom_IdAndUser_Id(Integer studyRoomId, Integer userId);

    void deleteByStudyRoom_Id(Integer id);
}
