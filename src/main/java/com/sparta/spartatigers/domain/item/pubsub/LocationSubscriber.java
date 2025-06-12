package com.sparta.spartatigers.domain.item.pubsub;

import java.nio.charset.StandardCharsets;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import com.sparta.spartatigers.domain.item.dto.response.RedisUpdateDto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
public class LocationSubscriber implements MessageListener {

    private final ObjectMapper objectMapper;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        RedisUpdateDto dto = deserialize(message);
        messagingTemplate.convertAndSend("/server/location-channel", dto);
    }

    private RedisUpdateDto deserialize(Message message) {
        try {
            String json = new String(message.getBody(), StandardCharsets.UTF_8);
            return objectMapper.readValue(json, RedisUpdateDto.class);
        } catch (JsonProcessingException e) {
            System.err.println("Redis 메시지 역직렬화 실패: " + e.getMessage());
            throw new RuntimeException("Redis 메시지 역직렬화 실패", e);
        }
    }
}
