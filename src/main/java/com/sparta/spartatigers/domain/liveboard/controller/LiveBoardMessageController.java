package com.sparta.spartatigers.domain.liveboard.controller;

import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.sparta.spartatigers.domain.liveboard.model.LiveBoardMessage;
import com.sparta.spartatigers.domain.liveboard.pubsub.RedisMessagePublisher;
import com.sparta.spartatigers.domain.liveboard.service.LiveBoardService;
import com.sparta.spartatigers.domain.user.model.CustomUserPrincipal;

@Slf4j
@Controller
@RequiredArgsConstructor
public class LiveBoardMessageController {

    private final RedisMessagePublisher redisPublisher;
    private final LiveBoardService liveBoardService;
    private final SimpMessageSendingOperations messagingTemplate;

    @MessageMapping("/liveboard/message")
    public void sendMessage(
            @Payload LiveBoardMessage message,
            @AuthenticationPrincipal CustomUserPrincipal principal) {
        log.info("sender id: {}", principal.getUsername());
        String roomId = message.getRoomId();
        ChannelTopic topic = liveBoardService.getTopic(roomId);
        if (topic == null) {
            liveBoardService.enterRoom(roomId, messagingTemplate);
            topic = liveBoardService.getTopic(roomId);
        }
        redisPublisher.publish(topic, message);
    }
}
