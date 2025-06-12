package com.sparta.spartatigers.domain.liveboard.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.sparta.spartatigers.domain.user.model.CustomUserPrincipal;
import com.sparta.spartatigers.domain.user.model.entity.User;
import com.sparta.spartatigers.domain.user.repository.UserRepository;
import com.sparta.spartatigers.global.token.JwtAuthenticationToken;
import com.sparta.spartatigers.global.token.JwtProvider;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenChannelInterceptor implements ChannelInterceptor {

    //    private final TokenProvider tokenProvider;
    private final UserRepository userRepository;

    @Autowired private JwtProvider jwtProvider;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String bearerToken = extractToken(accessor);

            if (!StringUtils.hasText(bearerToken)) {
                return message;
            }

            String token = bearerToken.replace("Bearer ", "");

			// TODO 현재 로컬에서 웹소켓 연결 시 secret 키가 맞지 않다고 함
			//  UnExpected Exception When Token Validating:
			//  JWT signature does not match locally computed signature.
			//  JWT validity cannot be asserted and should not be trusted.
            if (jwtProvider.validateToken(token)) {
                Long userId = jwtProvider.getUserIdFromToken(token);
                User user = userRepository.findByIdOrElseThrow(userId);
                log.info("id: {}, name: {}", user.getId(), user.getNickname());

                CustomUserPrincipal principal = new CustomUserPrincipal(user);
                Authentication auth =
                        new JwtAuthenticationToken(principal, principal.getAuthorities());

                accessor.setUser(auth);
                log.info("WebSocket 연결 인증 성공 - User ID : {}", userId);
            } else {
                log.warn("WebSocket 연결 인증 실패 = Invalid Token");
            }
        }
        return message;
    }

    private String extractToken(StompHeaderAccessor accessor) {
        return accessor.getFirstNativeHeader("Authorization");
    }
}
