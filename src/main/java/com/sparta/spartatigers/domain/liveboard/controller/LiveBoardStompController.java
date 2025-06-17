package com.sparta.spartatigers.domain.liveboard.controller;

import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;

import com.sparta.spartatigers.domain.liveboard.model.LiveBoardMessage;
import com.sparta.spartatigers.domain.liveboard.service.LiveBoardRedisService;
import com.sparta.spartatigers.domain.liveboard.service.LiveBoardRoomService;

@Controller
@RequiredArgsConstructor
public class LiveBoardStompController {

    private final LiveBoardRedisService liveBoardRedisService;
    private final LiveBoardRoomService liveBoardRoomService;

    // 채팅
    @MessageMapping("/liveboard/message")
    public void sendMessage(LiveBoardMessage message, Authentication authentication) {
        liveBoardRedisService.handleMessage(message, authentication);
    }

    // 입장
    @MessageMapping("/liveboard/enter")
    public void enterLiveBoard(Message<LiveBoardMessage> message, Authentication authentication) {
        liveBoardRedisService.enterRoom(message, authentication);
    }

    // 퇴장
    @MessageMapping("/liveboard/exit")
    public void exitLiveBoard(Message<LiveBoardMessage> message) {
        liveBoardRedisService.exitRoom(message);
    }
}
