package com.sparta.spartatigers.global.interceptor;

import java.security.Principal;
import java.util.Map;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

import com.sparta.spartatigers.domain.user.model.CustomUserPrincipal;

@Slf4j
@Component
public class AuthChannelInterceptor implements ChannelInterceptor {

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null
                && (StompCommand.CONNECT.equals(accessor.getCommand())
                        || StompCommand.SEND.equals(accessor.getCommand())
                        || StompCommand.SUBSCRIBE.equals(accessor.getCommand()))) {

            Principal principal = accessor.getUser();
            if (principal == null) {
                log.warn("WebSocket principal is null.");
                return message;
            }

            Long userId = null;

            if (principal instanceof OAuth2AuthenticationToken oauthToken) {
                Map<String, Object> attributes = oauthToken.getPrincipal().getAttributes();
                Object idAttribute = attributes.get("id");
                userId = Long.valueOf(idAttribute.toString());

                Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
                if (sessionAttributes != null) {
                    sessionAttributes.put("userId", userId);
                    log.info("OAuth 사용자 ID '{}'", userId);
                }

            } else if (principal instanceof UsernamePasswordAuthenticationToken authToken) {
                Object details = authToken.getPrincipal();
                if (details instanceof CustomUserPrincipal customUserPrincipal) {
                    userId = customUserPrincipal.getUser().getId();

                    Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
                    if (sessionAttributes != null) {
                        sessionAttributes.put("userId", userId);
                        log.info("일반 로그인 사용자 ID '{}'", userId);
                    } else {
                        log.warn("Session attributes가 null입니다.");
                    }
                } else {
                    log.warn("CustomUserPrincipal이 아닙니다: {}", details.getClass().getName());
                }
            }

            if (userId != null) {
                Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
                if (sessionAttributes != null) {
                    sessionAttributes.put("userId", userId);
                    log.info("✅ WebSocket 세션에 userId 저장됨: {}", userId);
                } else {
                    log.warn("세션 attribute가 null입니다. userId 저장 실패");
                }
            } else {
                log.warn("userId 추출 실패");
            }
        }

        return message;
    }
}
