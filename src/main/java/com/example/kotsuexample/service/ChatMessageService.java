package com.example.kotsuexample.service;

import com.example.kotsuexample.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor

public class ChatMessageService {
    private final ChatMessageRepository chatMessageRepository;
}
