package com.example.kotsuexample.repository;

import com.example.kotsuexample.entity.ChatRoomMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomMemberRepository extends JpaRepository<ChatRoomMember, Integer> {
}
