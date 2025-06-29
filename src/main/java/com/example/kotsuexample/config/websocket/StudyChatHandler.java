package com.example.kotsuexample.config.websocket;

import com.example.kotsuexample.config.redis.RedisPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
@RequiredArgsConstructor
public class StudyChatHandler extends TextWebSocketHandler {

    private final RedisPublisher redisPublisher;
    private final ChatSessionManager sessionManager;

    // WebSocket 연결 성립 시 세션 등록 (roomId, userId)
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String roomId = getRoomId(session);
        String userId = getUserId(session);
        sessionManager.addSession(roomId, session);
        session.getAttributes().put("userId", userId); // (선택) Attribute에 저장
    }

    // 텍스트 메시지 수신 시 Redis로 publish (핵심 로직)
    @Override
    protected void handleTextMessage(WebSocketSession session, org.springframework.web.socket.TextMessage message) throws Exception {
        String roomId = getRoomId(session);
        // Redis 채널명: chatroom:roomId (ex: chatroom:123)
        redisPublisher.publish("chatroom:" + roomId, message.getPayload());
    }

    // 연결 해제 시 세션 삭제
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String roomId = getRoomId(session);
        sessionManager.removeSession(roomId, session);
    }

    // (필요시) 바이너리 메시지도 분기 가능하지만, 대부분 텍스트만 사용
    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {
        // (P2P 미디어 데이터는 WebRTC에서 직접 다룸. 보통 이건 비워둬도 OK)
    }

    // ===== 쿼리스트링 파싱 유틸 =====
    private String getRoomId(WebSocketSession session) {
        // ws://.../ws/study?roomId=123&userId=5
        // 아래 코드로 파싱
        return sessionManager.getQueryParam(session, "roomId");
    }

    private String getUserId(WebSocketSession session) {
        return sessionManager.getQueryParam(session, "userId");
    }
}
