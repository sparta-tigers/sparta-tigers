package com.sparta.spartatigers.domain.liveboard.controller;

import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;

import com.sparta.spartatigers.domain.liveboard.model.LiveBoardMessage;
import com.sparta.spartatigers.domain.liveboard.pubsub.RedisMessagePublisher;
import com.sparta.spartatigers.domain.liveboard.service.LiveBoardRedisService;

@Controller
@RequiredArgsConstructor
public class LiveBoardMessageController {

    private final RedisMessagePublisher redisPublisher;
    private final LiveBoardRedisService liveBoardRedisService;
    private final SimpMessageSendingOperations messagingTemplate;

    @MessageMapping("/liveboard/message")
    public void sendMessage(LiveBoardMessage message) {
        String roomId = message.getRoomId();
        ChannelTopic topic = liveBoardRedisService.getTopic(roomId);
        if (topic == null) {
            liveBoardRedisService.enterRoom(roomId, messagingTemplate);
            topic = liveBoardRedisService.getTopic(roomId);
        }
        redisPublisher.publish(topic, message);
    }
}
