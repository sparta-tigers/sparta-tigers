package com.sparta.spartatigers.domain.chatroom.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;

import com.sparta.spartatigers.domain.chatroom.config.StompPrincipal;
import com.sparta.spartatigers.domain.chatroom.dto.request.ChatMessageRequest;
import com.sparta.spartatigers.domain.chatroom.pubsub.RedisDirectMessagePublisher;
import com.sparta.spartatigers.domain.user.model.CustomUserPrincipal;

@Controller
@RequiredArgsConstructor
public class ChatMessageController {

    private final RedisDirectMessagePublisher redisPublisher;

    @MessageMapping("/directRoom/send")
    public void sendMessage(ChatMessageRequest request, Authentication authentication) {
        Object principalObj = authentication.getPrincipal();
        Long senderId;

        if (principalObj instanceof StompPrincipal principal) {
            senderId = Long.parseLong(principal.getName());
        } else if (principalObj instanceof CustomUserPrincipal principal) {
            senderId = principal.getUser().getId();
        } else {
            throw new IllegalStateException(
                    "Unsupported principal type: " + principalObj.getClass());
        }

        redisPublisher.publish("directroom:" + request.getRoomId(), senderId, request);
    }
}
