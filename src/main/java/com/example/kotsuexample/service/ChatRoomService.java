package com.example.kotsuexample.service;

import com.example.kotsuexample.dto.ChatRoomRequest;
import com.example.kotsuexample.dto.ChatRoomResponse;
import com.example.kotsuexample.dto.GroupChatRoomRequest;
import com.example.kotsuexample.entity.ChatRoom;
import com.example.kotsuexample.entity.ChatRoomMember;
import com.example.kotsuexample.entity.User;
import com.example.kotsuexample.entity.enums.ChatRoomType;
import com.example.kotsuexample.exception.ChatRoomNotFoundException;
import com.example.kotsuexample.repository.ChatRoomMemberRepository;
import com.example.kotsuexample.repository.ChatRoomRepository;
import com.example.kotsuexample.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final UserRepository userRepository;

    public ChatRoomResponse getOrCreateSingleRoom(ChatRoomRequest request) {
        Integer userA = request.getRequesterId();
        Integer userB = request.getTargetId();

        Optional<ChatRoom> existingRoom = chatRoomRepository.findSingleRoomByUsers(userA, userB);
        if (existingRoom.isPresent()) {
            return ChatRoomResponse.of(existingRoom.get(), getMemberInfo(existingRoom.get().getId()));
        }

        // 1. 채팅방 생성
        ChatRoom room = chatRoomRepository.save(ChatRoom.builder()
                .type(ChatRoomType.SINGLE)
                .createdAt(LocalDateTime.now())
                .build());

        // 2. 채팅방 멤버 등록
        chatRoomMemberRepository.saveAll(List.of(
                ChatRoomMember.builder()
                        .chatRoomId(room.getId())
                        .userId(userA)
                        .joinedAt(LocalDateTime.now())
                        .build(),
                ChatRoomMember.builder()
                        .chatRoomId(room.getId())
                        .userId(userB)
                        .joinedAt(LocalDateTime.now())
                        .build()
        ));

        return ChatRoomResponse.of(room, getMemberInfo(room.getId()));
    }

    private List<ChatRoomResponse.MemberInfo> getMemberInfo(Integer roomId) {
        return chatRoomMemberRepository.findByChatRoomId(roomId).stream()
                .map(member -> {
                    User user = userRepository.findById(member.getUserId()).orElseThrow();
                    return ChatRoomResponse.MemberInfo.builder()
                            .userId(user.getId())
                            .nickname(user.getNickname())
                            .profileImageUrl(user.getProfileImage())
                            .build();
                })
                .toList();
    }

    public List<Integer> getMemberIds(Integer roomId) {
        if (!chatRoomRepository.existsById(roomId)) throw new ChatRoomNotFoundException("채팅방이 존재하지 않거나 유효하지 않습니다.");

        return chatRoomMemberRepository.findByChatRoomId(roomId)
                .stream()
                .map(ChatRoomMember::getUserId)
                .toList();
    }

    public ChatRoomResponse getOrCreateGroupRoom(GroupChatRoomRequest req) {
        // 1. 이미 해당 스터디방의 그룹채팅이 있으면 조회
        Optional<ChatRoom> existingRoomOpt = chatRoomRepository.findByTypeAndStudyRoomId(ChatRoomType.GROUP, req.getStudyRoomId());
        if (existingRoomOpt.isPresent()) {
            return ChatRoomResponse.of(existingRoomOpt.get(), getMemberInfo(existingRoomOpt.get().getId()));
        }

        // 2. 없으면 새로 생성
        ChatRoom room = chatRoomRepository.save(
                ChatRoom.builder()
                        .type(ChatRoomType.GROUP)
                        .createdAt(LocalDateTime.now())
                        .studyRoomId(req.getStudyRoomId())
                        .build()
        );

        // 3. 멤버 등록
        List<ChatRoomMember> members = req.getMemberIds().stream()
                .map(userId -> ChatRoomMember.builder()
                        .chatRoomId(room.getId())
                        .userId(userId)
                        .joinedAt(LocalDateTime.now())
                        .build())
                .toList();
        chatRoomMemberRepository.saveAll(members);

        // 4. 응답 생성
        return ChatRoomResponse.of(room, getMemberInfo(room.getId()));
    }

    public ChatRoomType getRoomType(Integer chatRoomId) {
        System.out.println("chatRoomId = " + chatRoomId);

        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new IllegalArgumentException("ChatRoom not found: " + chatRoomId));
        return chatRoom.getType();
    }
}
