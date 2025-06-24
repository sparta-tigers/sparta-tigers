package com.sparta.spartatigers.domain.chatroom.pubsub;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.sparta.spartatigers.domain.chatroom.dto.response.ChatMessageResponse;
import com.sparta.spartatigers.domain.chatroom.dto.response.RedisMessage;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisDirectMessageSubscriber implements MessageListener {

    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String body = new String(message.getBody(), StandardCharsets.UTF_8);
            RedisMessage payload = objectMapper.readValue(body, RedisMessage.class);

            ChatMessageResponse response =
                    new ChatMessageResponse(
                            payload.getRoomId(),
                            payload.getSenderId(),
                            payload.getSenderNickname(),
                            payload.getMessage(),
                            LocalDateTime.parse(payload.getSentAt()));

            messagingTemplate.convertAndSend("/server/directRoom/" + payload.getRoomId(), response);
            log.info("Redis 메시지 WebSocket 전송 완료");

        } catch (Exception e) {
            log.error("RedisDirectMessageSubscriber 오류 발생", e);
        }
    }
}
