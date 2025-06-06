package com.example.kotsuexample.repository;

import com.globalin.kotsukotsu.entity.StudyRoomMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyRoomMemberRepository extends JpaRepository<StudyRoomMember, Integer> {
}
