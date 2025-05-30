package com.sparta.spartatigers.domain.liveboard.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;

import com.sparta.spartatigers.domain.liveboard.model.LiveBoardMessage;

@Controller
@RequiredArgsConstructor
public class LiveBoardStompController {

    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat/message")
    public void sendMessage(LiveBoardMessage message) {
        String roomId = message.getRoomId();
        messagingTemplate.convertAndSend("sub/chat/room" + roomId, message);
    }
}
