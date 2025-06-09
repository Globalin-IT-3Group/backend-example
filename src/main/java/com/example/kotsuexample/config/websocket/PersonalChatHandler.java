package com.example.kotsuexample.config.websocket;

import com.example.kotsuexample.config.redis.RedisPublisher;
import com.example.kotsuexample.dto.ChatMessageDTO;
import com.example.kotsuexample.entity.enums.MessageType;
import com.example.kotsuexample.service.ChatReadService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@RequiredArgsConstructor
public class PersonalChatHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper;
    private final ChatSessionManager sessionManager;
    private final RedisPublisher redisPublisher;
    private final ChatReadService chatReadService;

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {
        String roomId = getQueryParam(session, "roomId");
        String senderId = getQueryParam(session, "userId"); // 세션에 유저정보가 없다면 쿼리 파라미터로 받기
//
//        // 1. 파일 저장 (예: local → /uploads, 또는 AWS S3 업로드)
//        String fileUrl = fileUploadService.saveFile(message.getPayload().asByteBuffer());
//
//        // 2. Redis 발행
//        ChatMessageDTO dto = new ChatMessageDTO();
//        dto.setChatRoomId(Integer.parseInt(roomId));
//        dto.setSenderId(Integer.parseInt(senderId));
//        dto.setMessageType(MessageType.FILE); // 또는 IMAGE
//        dto.setMessage(fileUrl);
//        redisPublisher.publish("chatroom:" + roomId, objectMapper.writeValueAsString(dto));
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String roomId = getQueryParam(session, "roomId");
        String userId = getQueryParam(session, "userId"); // 쿼리 파라미터에 userId 포함되어야 함

        sessionManager.addSession(roomId, session);

        // 읽음 처리
        chatReadService.markChatAsRead(Integer.valueOf(roomId), Integer.valueOf(userId));
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        redisPublisher.publish("chatroom:" + getQueryParam(session, "roomId"), message.getPayload());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String roomId = getQueryParam(session, "roomId");
        sessionManager.removeSession(roomId, session);
    }

    private String getQueryParam(WebSocketSession session, String key) {
        return UriComponentsBuilder.fromUri(session.getUri()).build().getQueryParams().getFirst(key);
    }
}
