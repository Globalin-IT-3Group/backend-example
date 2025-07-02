package com.example.kotsuexample.service;

import com.example.kotsuexample.dto.ChatRoomSummary;
import com.example.kotsuexample.dto.UserResponse;
import com.example.kotsuexample.entity.*;
import com.example.kotsuexample.entity.enums.ChatRoomType;
import com.example.kotsuexample.exception.StudyDataNotFoundException;
import com.example.kotsuexample.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatReadService {

    private final ChatReadStatusRepository chatReadStatusRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final StudyRoomRepository studyRoomRepository;
    private final UserService userService;

    // 읽음 처리
    @Transactional
    public void markChatAsRead(Integer roomId, Integer userId, LocalDateTime lastReadAt) {
        ChatReadStatus status = chatReadStatusRepository
                .findByChatRoomIdAndUserId(roomId, userId)
                .orElse(ChatReadStatus.builder()
                        .chatRoomId(roomId)
                        .userId(userId)
                        .build());

        status.updateLastReadAt(lastReadAt);
        chatReadStatusRepository.save(status);
    }

    // 미확인 메시지 수 조회
    public int getUnreadCount(Integer roomId, Integer userId) {
        Optional<LocalDateTime> lastReadOpt = chatReadStatusRepository
                .findByChatRoomIdAndUserId(roomId, userId)
                .map(ChatReadStatus::getLastReadAt);

        if (lastReadOpt.isPresent()) {
            // 기존처럼 마지막 읽은 시각 이후 메시지 수 카운트
            return chatMessageRepository.countByChatRoomIdAndSentAtAfterAndSenderIdNot(
                    roomId, lastReadOpt.get(), userId
            );
        } else {
            // 한 번도 읽지 않은 경우 → 내가 보낸 것 빼고 전부 unread
            return chatMessageRepository.countByChatRoomIdAndSenderIdNot(
                    roomId, userId
            );
        }
    }

    public ChatRoomSummary getChatRoomSummary(Integer chatRoomId, Integer userId) {
        int unread = getUnreadCount(chatRoomId, userId);
        Optional<ChatMessage> lastMessage = chatMessageRepository.findTopByChatRoomIdOrderBySentAtDesc(chatRoomId);

        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new StudyDataNotFoundException("채팅방이 존재하지 않습니다."));

        List<Integer> memberIds = chatRoomMemberRepository.findByChatRoomId(chatRoomId).stream()
                .map(ChatRoomMember::getUserId)
                .filter(id -> !id.equals(userId))
                .toList();

        List<UserResponse> otherUsers;
        if (memberIds.isEmpty()) {
            UserResponse self = userService.getSimpleUserInfoById(userId);
            otherUsers = List.of(self);
        } else if (memberIds.size() == 1) {
            UserResponse other = userService.getSimpleUserInfoById(memberIds.get(0));
            otherUsers = List.of(other);
        } else {
            otherUsers = memberIds.stream()
                    .map(userService::getSimpleUserInfoById)
                    .toList();
        }

        // GROUP일 경우 스터디 정보 조회
        String studyRoomName = null;
        String studyRoomImageUrl = null;
        if (chatRoom.getType() == ChatRoomType.GROUP) {
            if (chatRoom.getStudyRoomId() != null) {
                StudyRoom studyRoom = studyRoomRepository.findById(chatRoom.getStudyRoomId())
                        .orElseThrow(() -> new StudyDataNotFoundException("스터디룸이 존재하지 않습니다."));
                studyRoomName = studyRoom.getName();
                studyRoomImageUrl = studyRoom.getImageUrl(); // 이 필드가 StudyRoom 엔티티에 있어야 함
            }
        }

        return ChatRoomSummary.builder()
                .roomId(chatRoomId)
                .unreadCount(unread)
                .lastMessage(lastMessage.map(ChatMessage::getMessage).orElse(""))
                .lastMessageAt(lastMessage.map(ChatMessage::getSentAt).orElse(null))
                .otherUsers(otherUsers)
                .roomType(chatRoom.getType())
                .studyRoomName(studyRoomName)
                .studyRoomImageUrl(studyRoomImageUrl)
                .build();
    }

    public List<ChatRoomSummary> getAllChatRoomSummaries(Integer userId) {
        return chatRoomMemberRepository.findByUserId(userId).stream()
                .map(member -> getChatRoomSummary(member.getChatRoomId(), userId))
                .toList();
    }

    public int getUnreadMemberCountForMessage(Integer roomId, Integer messageId) {
        // 1. 메시지 조회
        ChatMessage message = chatMessageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("메시지를 찾을 수 없습니다."));

        // 2. 채팅방 모든 멤버 조회
        List<ChatRoomMember> members = chatRoomMemberRepository.findByChatRoomId(roomId);

        // 3. 각 멤버의 읽음 정보 조회 (Map<userId, lastReadAt>)
        Map<Integer, LocalDateTime> lastReadMap = chatReadStatusRepository
                .findByChatRoomId(roomId).stream()
                .collect(Collectors.toMap(ChatReadStatus::getUserId, ChatReadStatus::getLastReadAt));

        int unreadCount = 0;
        for (ChatRoomMember member : members) {
            Integer userId = member.getUserId();
            if (userId.equals(message.getSenderId())) continue; // 본인은 제외

            LocalDateTime lastReadAt = lastReadMap.get(userId);
            // lastReadAt이 없거나, 메시지보다 이전이면 아직 안 읽음
            if (lastReadAt == null || lastReadAt.isBefore(message.getSentAt())) {
                unreadCount++;
            }
        }
        return unreadCount;
    }
}
