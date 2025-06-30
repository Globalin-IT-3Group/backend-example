package com.example.kotsuexample.repository;

import com.example.kotsuexample.entity.ChatRoom;
import com.example.kotsuexample.entity.enums.ChatRoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Integer> {
    @Query("""
        SELECT cr
        FROM ChatRoom cr
        WHERE cr.type = 'SINGLE'
          AND cr.id IN (
            SELECT crm.chatRoomId
            FROM ChatRoomMember crm
            WHERE crm.userId IN (:userA, :userB)
            GROUP BY crm.chatRoomId
            HAVING COUNT(DISTINCT crm.userId) = 2
          )
        """)
    Optional<ChatRoom> findSingleRoomByUsers(@Param("userA") Integer userA, @Param("userB") Integer userB);

    Optional<ChatRoom> findByTypeAndStudyRoomId(ChatRoomType chatRoomType, Integer studyRoomId);

    // chatRoomRepository
    @Query("select c.id from ChatRoom c where c.studyRoomId = :studyRoomId and c.type = 'GROUP'")
    Optional<Integer> findGroupChatRoomIdByStudyRoomId(@Param("studyRoomId") Integer studyRoomId);
}
