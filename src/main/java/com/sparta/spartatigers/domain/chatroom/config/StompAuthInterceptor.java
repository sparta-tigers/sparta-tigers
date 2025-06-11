package com.sparta.spartatigers.domain.chatroom.config;

import java.util.List;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import com.sparta.spartatigers.domain.user.model.entity.User;
import com.sparta.spartatigers.domain.user.service.CustomUserDetailsService;
import com.sparta.spartatigers.global.util.JwtUtil;

import io.jsonwebtoken.Claims;

@Component
@RequiredArgsConstructor
public class StompAuthInterceptor implements ChannelInterceptor {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;
    private final UserSessionRegistry userSessionRegistry;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String token = accessor.getFirstNativeHeader("Authorization");

            System.out.println("Authorization header: " + token);

            if (token == null || !token.startsWith("Bearer ")) {
                throw new IllegalArgumentException("Authorization header missing or malformed");
            }
            token = token.substring(7);

            Claims claims = jwtUtil.validateToken(token);
            if (claims == null) {
                throw new IllegalArgumentException("Invalid JWT token");
            }

            String email = claims.getSubject();
            User user = userDetailsService.loadUserByUsername(email).getUser();

            StompPrincipal principal = new StompPrincipal(user.getId(), user.getNickname());
            accessor.setUser(principal);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            principal, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 세션-유저 매핑 저장소에 등록
            userSessionRegistry.registerSession(user.getId(), accessor.getSessionId());

            System.out.println("✅ 연결된 userId: " + user.getId());
            System.out.println("✅ 세션 ID: " + accessor.getSessionId());
        }

        return message;
    }
}
