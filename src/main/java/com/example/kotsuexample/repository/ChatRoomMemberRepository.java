package com.example.kotsuexample.repository;

import com.globalin.kotsukotsu.entity.ChatRoomMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomMemberRepository extends JpaRepository<ChatRoomMember, Integer> {
}
