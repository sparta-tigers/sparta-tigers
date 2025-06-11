package com.sparta.spartatigers.domain.chatroom.config;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

@Component
public class UserSessionRegistry {

    private final ConcurrentHashMap<Long, Set<String>> userSessions = new ConcurrentHashMap<>();

    // 추가: sessionId -> userId 매핑
    private final ConcurrentHashMap<String, Long> sessionIdToUserId = new ConcurrentHashMap<>();

    public void registerSession(Long userId, String sessionId) {
        userSessions.compute(
                userId,
                (key, sessions) -> {
                    if (sessions == null) {
                        sessions = ConcurrentHashMap.newKeySet();
                    }
                    sessions.add(sessionId);
                    return sessions;
                });
        // 매핑 추가
        sessionIdToUserId.put(sessionId, userId);
    }

    public void unregisterSession(Long userId, String sessionId) {
        userSessions.computeIfPresent(
                userId,
                (key, sessions) -> {
                    sessions.remove(sessionId);
                    if (sessions.isEmpty()) {
                        return null;
                    }
                    return sessions;
                });
        // 매핑 제거
        sessionIdToUserId.remove(sessionId);
    }

    public boolean isUserConnected(Long userId) {
        Set<String> sessions = userSessions.get(userId);
        return sessions != null && !sessions.isEmpty();
    }

    // 추가: sessionId로 userId 조회
    public Long getUserIdBySessionId(String sessionId) {
        return sessionIdToUserId.get(sessionId);
    }
}
