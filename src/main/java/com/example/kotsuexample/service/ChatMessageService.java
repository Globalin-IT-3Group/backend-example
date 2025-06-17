package com.example.kotsuexample.service;

import com.example.kotsuexample.dto.ChatMessageDTO;
import com.example.kotsuexample.entity.ChatMessage;
import com.example.kotsuexample.entity.ChatReadStatus;
import com.example.kotsuexample.entity.ChatRoomMember;
import com.example.kotsuexample.repository.ChatMessageRepository;
import com.example.kotsuexample.repository.ChatReadStatusRepository;
import com.example.kotsuexample.repository.ChatRoomMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor

public class ChatMessageService {
    private final ChatMessageRepository chatMessageRepository;
    private final ChatReadStatusRepository chatReadStatusRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;

    public List<ChatMessageDTO> getChatMessagesWithReadStatus(Integer roomId, Integer myUserId) {
        // 1. 메시지 전체 조회
        List<ChatMessage> messages = chatMessageRepository.findByChatRoomIdOrderBySentAtAsc(roomId);

        // 2. 채팅방 멤버들 조회 (1:1 채팅 가정)
        List<Integer> members = chatRoomMemberRepository.findByChatRoomId(roomId)
                .stream().map(ChatRoomMember::getUserId).toList();

        Integer opponentId = members.stream()
                .filter(id -> !id.equals(myUserId))
                .findFirst()
                .orElse(null);

        // 3. 상대방의 마지막 읽음 시각
        LocalDateTime opponentLastReadAt = chatReadStatusRepository
                .findByChatRoomIdAndUserId(roomId, opponentId)
                .map(ChatReadStatus::getLastReadAt)
                .orElse(LocalDateTime.MIN);

        // 4. 메시지별로 isRead 계산
        return messages.stream()
                .map(msg -> ChatMessageDTO.builder()
                        .id(msg.getId())
                        .chatRoomId(msg.getChatRoomId())
                        .senderId(msg.getSenderId())
                        .message(msg.getMessage())
                        .messageType(msg.getMessageType())
                        .sentAt(msg.getSentAt().toString())
                        // 내가 보낸 메시지라면: 상대방의 읽음시각 이후라면 "안 읽음" (isRead=false), 아니면 "읽음"(isRead=true)
                        .isRead(!msg.getSenderId().equals(myUserId) || !msg.getSentAt().isAfter(opponentLastReadAt)) // 내가 아닌 메시지는 항상 읽음(true)
                        .build())
                .collect(Collectors.toList());
    }
}
