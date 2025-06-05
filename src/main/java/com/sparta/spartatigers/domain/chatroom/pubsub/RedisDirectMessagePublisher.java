package com.sparta.spartatigers.domain.chatroom.pubsub;

import com.sparta.spartatigers.domain.chatroom.dto.request.ChatMessageRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisDirectMessagePublisher {

	private final RedisTemplate<String, Object> redisTemplate;

	public void publish(String topic, ChatMessageRequest message) {
		redisTemplate.convertAndSend(topic, message);
	}
}
