package com.sparta.spartatigers.domain.chatroom.pubsub;

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import com.sparta.spartatigers.domain.chatroom.dto.request.ChatMessageRequest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
public class RedisDirectMessagePublisher {

    private final StringRedisTemplate redisStringTemplate;
    private final ObjectMapper objectMapper;

    public void publish(String topic, Long senderId, ChatMessageRequest message) {
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("roomId", message.getRoomId());
            payload.put("message", message.getMessage());
            payload.put("senderId", senderId);

            String json = objectMapper.writeValueAsString(payload);
            redisStringTemplate.convertAndSend(topic, json);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
