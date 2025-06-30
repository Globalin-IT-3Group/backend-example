package com.example.kotsuexample.config.websocket;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChatSessionManager {

    private final Map<String, Set<WebSocketSession>> sessionMap = new ConcurrentHashMap<>();

    public void addSession(String roomId, WebSocketSession session) {
        sessionMap.computeIfAbsent(roomId, k -> ConcurrentHashMap.newKeySet()).add(session);
    }

    public void removeSession(String roomId, WebSocketSession session) {
        Set<WebSocketSession> sessions = sessionMap.get(roomId);
        if (sessions != null) {
            sessions.remove(session);
            if (sessions.isEmpty()) sessionMap.remove(roomId);
        }
    }

    public Set<WebSocketSession> getSessions(String roomId) {
        return sessionMap.getOrDefault(roomId, Collections.emptySet());
    }

    // 해당 방에 userId 연결되어 있는지 확인
    public boolean isUserConnected(String roomId, Integer userId) {
        return getSessions(roomId).stream().anyMatch(session -> {
            String connectedUserId = getUserId(session);
            return userId.toString().equals(connectedUserId);
        });
    }

    // 쿼리스트링에서 userId 추출 (또는 attribute에서 추출)
    public String getUserId(WebSocketSession session) {
        // Attribute에 있을 경우 우선 (afterConnectionEstablished에서 저장했다면)
        Object attr = session.getAttributes().get("userId");
        if (attr != null) return attr.toString();
        // 쿼리 파라미터에서 추출
        return getQueryParam(session, "userId");
    }

    public String getQueryParam(WebSocketSession session, String key) {
        return UriComponentsBuilder.fromUri(Objects.requireNonNull(session.getUri())).build().getQueryParams().getFirst(key);
    }

    public boolean hasActiveSession(String roomId) {
        Set<WebSocketSession> set = sessionMap.get(roomId);
        return set != null && set.stream().anyMatch(WebSocketSession::isOpen);
    }
}
