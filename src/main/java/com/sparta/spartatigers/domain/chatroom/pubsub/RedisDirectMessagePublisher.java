package com.sparta.spartatigers.domain.chatroom.pubsub;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import com.sparta.spartatigers.domain.chatroom.dto.request.ChatMessageRequest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
public class RedisDirectMessagePublisher {

    private final RedisTemplate<String, String> redisStringTemplate;
    private final ObjectMapper objectMapper;

    public void publish(String topic, ChatMessageRequest message) {
        try {
            String json = objectMapper.writeValueAsString(message);
            System.out.println("Publishing to topic: " + topic + " message: " + json);
            redisStringTemplate.convertAndSend(topic, json);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
