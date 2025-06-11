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

    public boolean isUserConnected(String roomId, Integer userId) {
        return getSessions(roomId).stream().anyMatch(session -> {
            String connectedUserId = getQueryParam(session, "userId");
            return userId.toString().equals(connectedUserId);
        });
    }

    public String getQueryParam(WebSocketSession session, String key) {
        return UriComponentsBuilder.fromUri(Objects.requireNonNull(session.getUri())).build().getQueryParams().getFirst(key);
    }
}
