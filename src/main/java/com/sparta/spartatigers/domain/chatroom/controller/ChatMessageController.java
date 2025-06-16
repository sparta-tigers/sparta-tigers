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

    /**
     * 클라이언트가 "/directRoom/send" 경로로 메시지를 보내면 호출 인증 정보를 기반으로 보낸 사용자의 ID를 추출 해당 메시지를 Redis 채널에 발행
     *
     * @param request 클라이언트가 전송한 채팅 메시지 요청 정보
     * @param authentication 현재 인증된 사용자 정보를 담고 있는 객체
     * @throws IllegalStateException 지원되지 않는 Principal 타입일 경우 발생
     */
    @MessageMapping("/directRoom/send")
    public void sendMessage(ChatMessageRequest request, Authentication authentication) {
        Object principalObj = authentication.getPrincipal();
        Long senderId;

        if (principalObj instanceof StompPrincipal principal) {
            senderId = Long.parseLong(principal.getName());
        } else if (principalObj instanceof CustomUserPrincipal principal) {
            senderId = principal.getUser().getId();
        } else {
            throw new IllegalStateException("지원하지 않는 principal 타입: " + principalObj.getClass());
        }

        redisPublisher.publish("directroom:" + request.getRoomId(), senderId, request);
    }
}
