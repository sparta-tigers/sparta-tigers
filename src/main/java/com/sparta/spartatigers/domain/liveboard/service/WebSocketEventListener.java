package com.sparta.spartatigers.domain.liveboard.service;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import lombok.RequiredArgsConstructor;

import com.sparta.spartatigers.domain.liveboard.util.GlobalSessionIdGenerator;

@Component
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final LiveBoardRedisService liveBoardService;

    @EventListener
    public void handleDisconnect(SessionDisconnectEvent event) {
        String sessionId = event.getSessionId();
        String globalSessionId = GlobalSessionIdGenerator.generate(sessionId);

        liveBoardService.handleDisconnect(globalSessionId);
    }
}
