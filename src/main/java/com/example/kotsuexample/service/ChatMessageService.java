package com.example.kotsuexample.service;

import com.example.kotsuexample.entity.ChatMessage;
import com.example.kotsuexample.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor

public class ChatMessageService {
    private final ChatMessageRepository chatMessageRepository;

    public List<ChatMessage> getChatMessages(Integer roomId) {

        return chatMessageRepository.findByChatRoomIdOrderBySentAtAsc(roomId);
    }
}
