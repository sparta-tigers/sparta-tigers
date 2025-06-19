package com.sparta.spartatigers.domain.chatroom.listener;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import lombok.RequiredArgsConstructor;

import com.sparta.spartatigers.domain.chatroom.registry.RedisUserSessionRegistry;

@Component
@RequiredArgsConstructor
public class StompSessionEventListener {

    private final RedisUserSessionRegistry userSessionRegistry;

    /** stomp disconnect시 호출 */
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
