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

    /**
     * 주어진 senderId와 ChatMessageRequest의 내용으로 JSON 생성하고, 지정된 Redis 채널로 메시지를 발행합니다.
     *
     * @param topic Redis 채널 이름 (ex: "directroom:1")
     * @param senderId 메시지를 보낸 사용자의 ID
     * @param message 채팅 메시지 정보 (roomId, message 내용 포함)
     */
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
