package com.sparta.spartatigers.global.interceptor;

import java.security.Principal;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

import com.sparta.spartatigers.domain.chatroom.model.security.StompPrincipal;

@Slf4j
@Component
public class AuthChannelInterceptor implements ChannelInterceptor {

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            Principal principal = accessor.getUser();

            if (principal == null) {
                log.warn("WebSocket CONNECT 시점에 Principal이 존재하지 않습니다.");
                return message;
            }

            if (principal instanceof StompPrincipal stompPrincipal) {
                try {
                    String userIdString = stompPrincipal.getName();
                    accessor.getSessionAttributes().put("userId", Long.parseLong(userIdString));
                    return message;
                } catch (Exception e) {
                    log.warn("userId 변환중 예외가 발생 했습니다: {}", e.getCause(), e);
                    return message;
                }
            }

            log.warn(
                    "Principal 객체에서 CustomUserPrincipal을 추출하지 못했습니다: {}",
                    principal.getClass().getName());

            return message;
        }

        return message;
    }
}
