package com.sparta.spartatigers.domain.liveboard.service;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import com.sparta.spartatigers.domain.liveboard.util.GlobalSessionIdGenerator;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class WebSocketEventListener {

	private final LiveBoardRedisService liveBoardService;

	public void handleDisconnect (SessionDisconnectEvent event) {
		String sessionId = event.getSessionId();
		String globalSessionId = GlobalSessionIdGenerator.generate(sessionId);

		liveBoardService.handleDisconnect(globalSessionId);
	}
}
