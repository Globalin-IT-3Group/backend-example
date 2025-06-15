package com.example.kotsuexample.repository;

import com.example.kotsuexample.entity.Friend;
import com.example.kotsuexample.entity.enums.FriendStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FriendRepository extends JpaRepository<Friend, Integer> {

    // ✅ userId가 친구 요청자든 수락자든 관계없이, ACCEPTED 상태인 친구 목록을 모두 조회
    @Query("SELECT f FROM Friend f WHERE (f.requesterId = :userId OR f.addresseeId = :userId) AND f.status = 'ACCEPTED'")
    List<Friend> findAcceptedFriends(@Param("userId") Integer userId);

    // ✅ 두 사람 간의 친구 요청 상태 조회 (예: 친구 신청 중인지 확인할 때)
    Optional<Friend> findByRequesterIdAndAddresseeIdAndStatus(int requesterId, int addresseeId, FriendStatus status);

    List<Friend> findByRequesterIdAndStatus(Integer requesterId, FriendStatus status);
    List<Friend> findByAddresseeIdAndStatus(Integer addresseeId, FriendStatus status);

    Optional<Friend> findByRequesterIdAndAddresseeIdOrAddresseeIdAndRequesterIdAndStatus(
            Integer requesterId1, Integer addresseeId1,
            Integer requesterId2, Integer addresseeId2,
            FriendStatus status
    );

    @Query("SELECT f FROM Friend f WHERE " +
            "(f.requesterId = :userId AND f.addresseeId = :targetUserId) " +
            "OR (f.requesterId = :targetUserId AND f.addresseeId = :userId)")
    Optional<Friend> findRelation(@Param("userId") Integer userId, @Param("targetUserId") Integer targetUserId);
}
