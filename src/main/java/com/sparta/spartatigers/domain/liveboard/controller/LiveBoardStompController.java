package com.sparta.spartatigers.domain.liveboard.controller;

import java.security.Principal;

import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;

import com.sparta.spartatigers.domain.liveboard.model.LiveBoardMessage;
import com.sparta.spartatigers.domain.liveboard.service.LiveBoardRedisService;
import com.sparta.spartatigers.global.exception.WebSocketException;
import com.sparta.spartatigers.global.response.WebSocketErrorResponse;

@Controller
@RequiredArgsConstructor
public class LiveBoardStompController {

    private final LiveBoardRedisService liveBoardRedisService;

    // 채팅
    @MessageMapping("/liveboard/message")
    public void sendMessage(LiveBoardMessage message, Principal principal) {
        liveBoardRedisService.handleMessage(message, principal);
    }

    // 입장
    @MessageMapping("/liveboard/enter")
    public void enterLiveBoard(Message<LiveBoardMessage> message, Principal principal) {
        liveBoardRedisService.enterRoom(message, principal);
    }

    // 퇴장
    @MessageMapping("/liveboard/exit")
    public void exitLiveBoard(Message<LiveBoardMessage> message) {
        liveBoardRedisService.exitRoom(message);
    }

    // 예외 처리
    @MessageExceptionHandler(WebSocketException.class)
    @SendToUser("/liveboard/errors")
    public WebSocketErrorResponse handleWebSocketError(WebSocketException e) {
        return WebSocketErrorResponse.from(e.getCode());
    }
}
