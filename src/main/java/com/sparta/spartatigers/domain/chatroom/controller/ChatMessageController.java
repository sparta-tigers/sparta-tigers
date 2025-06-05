package com.sparta.spartatigers.domain.chatroom.controller;

import com.sparta.spartatigers.domain.chatroom.dto.request.ChatMessageRequest;
import com.sparta.spartatigers.domain.chatroom.pubsub.RedisDirectMessagePublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatMessageController {

	private final RedisDirectMessagePublisher redisPublisher;

	@MessageMapping("/chat/message")
	public void sendMessage(ChatMessageRequest request) {
		redisPublisher.publish("chatroom:" + request.getRoomId(), request);
	}
}

