package com.sparta.spartatigers.domain.chatroom.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;

import com.sparta.spartatigers.domain.chatroom.dto.request.ChatMessageRequest;
import com.sparta.spartatigers.domain.chatroom.pubsub.RedisDirectMessagePublisher;

@Controller
@RequiredArgsConstructor
public class ChatMessageController {

    private final RedisDirectMessagePublisher redisPublisher;

    @MessageMapping("/directRoom/send")
    public void sendMessage(ChatMessageRequest request) {
        redisPublisher.publish("directroom:" + request.getRoomId(), request);
    }
}
