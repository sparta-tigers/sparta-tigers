package com.sparta.spartatigers.domain.liveboard.controller;

import com.sparta.spartatigers.domain.liveboard.model.LiveBoardMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class LiveBoardStompController {

	private final SimpMessagingTemplate messagingTemplate;

	@MessageMapping("/chat/message")
	public void sendMessage(LiveBoardMessage message) {
		String roomId = message.getRoomId();
		messagingTemplate.convertAndSend("client/chat/room" + roomId, message);
	}
}
