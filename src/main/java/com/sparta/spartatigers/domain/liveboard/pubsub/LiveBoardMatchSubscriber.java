package com.sparta.spartatigers.domain.liveboard.pubsub;

import java.nio.charset.StandardCharsets;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.sparta.spartatigers.domain.liveboard.model.GameMessage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class LiveBoardMatchSubscriber implements MessageListener {

    private final ObjectMapper objectMapper;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            GameMessage gameMessage =
                    objectMapper.readValue(
                            new String(message.getBody(), StandardCharsets.UTF_8),
                            GameMessage.class);

            messagingTemplate.convertAndSend(
                    "/server/liveboard/room/" + gameMessage.getMatchId() + "/game", gameMessage);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
