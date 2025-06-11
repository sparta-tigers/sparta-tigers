package com.sparta.spartatigers.domain.chatroom.config;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class StompSessionEventListener {

    private final UserSessionRegistry userSessionRegistry;

    // STOMP CONNECT 시 호출 (또는 Interceptor preSend에서)
    public void registerSession(Long userId, String sessionId) {
        userSessionRegistry.registerSession(userId, sessionId);
    }

    // STOMP DISCONNECT 시 호출
    @EventListener
    public void handleSessionDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = sha.getSessionId();

        Long userId = userSessionRegistry.getUserIdBySessionId(sessionId);

        if (userId != null) {
            userSessionRegistry.unregisterSession(userId, sessionId);
        }
    }
}
