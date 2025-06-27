package com.example.kotsuexample.service;

import com.example.kotsuexample.dto.UserResponse;
import com.example.kotsuexample.entity.Friend;
import com.example.kotsuexample.entity.enums.FriendStatus;
import com.example.kotsuexample.exception.FriendNotFoundException;
import com.example.kotsuexample.repository.FriendRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FriendService {

    private final FriendRepository friendRepository;
    private final UserService userService;

    public List<UserResponse> getFriends(Integer userId) {
        List<Friend> friends = friendRepository.findAcceptedFriends(userId);

        return friends.stream()
                .map(friend -> {
                    Integer friendId = friend.getRequesterId().equals(userId)
                            ? friend.getAddresseeId()
                            : friend.getRequesterId();

                    return userService.getSimpleUserInfoById(friendId);
                })
                .toList();
    }

    public Boolean requestOrCancelFriend(Integer requesterId, Integer addresseeId) {
        Optional<Friend> friendOpt = friendRepository
                .findByRequesterIdAndAddresseeIdAndStatus(requesterId, addresseeId, FriendStatus.PENDING);

        if (friendOpt.isPresent()) {
            // 요청이 이미 존재하면 삭제 = 취소
            friendRepository.delete(friendOpt.get());
            return false; // 취소됨
        } else {
            // 요청이 없으면 새로 생성
            Friend friend = Friend.builder()
                    .requesterId(requesterId)
                    .addresseeId(addresseeId)
                    .status(FriendStatus.PENDING)
                    .build();
            friendRepository.save(friend);
            return true; // 요청됨
        }
    }

    // 내가 보낸 요청
    public List<UserResponse> getRequestedFriends(Integer userId) {
        List<Friend> requests = friendRepository.findByRequesterIdAndStatus(userId, FriendStatus.PENDING);
        return requests.stream()
                .map(friend -> userService.getSimpleUserInfoById(friend.getAddresseeId()))
                .toList();
    }

    // 내가 받은 요청
    public List<UserResponse> getPendingRequests(Integer userId) {
        List<Friend> requests = friendRepository.findByAddresseeIdAndStatus(userId, FriendStatus.PENDING);
        return requests.stream()
                .map(friend -> userService.getSimpleUserInfoById(friend.getRequesterId()))
                .toList();
    }

    public void deleteFriend(Integer userId, Integer friendId) {
        Friend friend = friendRepository
                .findByRequesterIdAndAddresseeIdOrAddresseeIdAndRequesterIdAndStatus(
                        userId, friendId, userId, friendId, FriendStatus.ACCEPTED
                )
                .orElseThrow(() -> new FriendNotFoundException("해당 친구 관계가 존재하지 않습니다."));

        friendRepository.delete(friend);
    }

    public Boolean acceptFriendRequest(Integer requesterId, Integer addresseeId) {
        Friend friend = friendRepository.findByRequesterIdAndAddresseeIdAndStatus(
                requesterId, addresseeId, FriendStatus.PENDING
        ).orElseThrow(() -> new FriendNotFoundException("해당 친구 요청이 존재하지 않습니다."));

        friend.changeFriendStatus(FriendStatus.ACCEPTED);
        friendRepository.save(friend);
        return true;
    }

    public Friend getFriendRelation(Integer userId, Integer targetUserId) {
        if (userId.equals(targetUserId)) return null;

        return friendRepository.findRelation(userId, targetUserId).orElse(null);
    }

    // 친구 요청 거절(삭제)
    public void rejectFriendRequest(Integer requesterId, Integer addresseeId) {
        Friend friend = friendRepository.findByRequesterIdAndAddresseeIdAndStatus(
                requesterId, addresseeId, FriendStatus.PENDING
        ).orElseThrow(() -> new FriendNotFoundException("해당 친구 요청이 존재하지 않습니다."));

        friendRepository.delete(friend);
    }
}
