package com.sparta.spartatigers.domain.liveboard.pubsub;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.sparta.spartatigers.domain.liveboard.model.LiveBoardMessage;

import com.fasterxml.jackson.databind.ObjectMapper;

@Slf4j
@RequiredArgsConstructor
@Service
public class RedisMessageSubscriber implements MessageListener {

    private final ObjectMapper objectMapper;
    private final RedisTemplate<String, String> redisTemplate;
    private final SimpMessageSendingOperations messagingTemplate;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            // redis에서 발행된 데이터를 json으로 deseralize
            String publishMessage =
                    (String) redisTemplate.getStringSerializer().deserialize(message.getBody());
            LiveBoardMessage liveBoardMessage =
                    objectMapper.readValue(publishMessage, LiveBoardMessage.class);

            messagingTemplate.convertAndSend(
                    "/server/liveboard/room/" + liveBoardMessage.getRoomId(), liveBoardMessage);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
