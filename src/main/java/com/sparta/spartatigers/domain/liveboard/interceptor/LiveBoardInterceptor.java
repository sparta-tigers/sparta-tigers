package com.sparta.spartatigers.domain.liveboard.interceptor;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.sparta.spartatigers.domain.chatroom.model.security.StompPrincipal;
import com.sparta.spartatigers.domain.liveboard.util.GlobalSessionIdGenerator;
import com.sparta.spartatigers.domain.user.model.CustomUserPrincipal;
import com.sparta.spartatigers.domain.user.model.entity.User;
import com.sparta.spartatigers.domain.user.service.CustomUserDetailsService;
import com.sparta.spartatigers.global.util.JwtUtil;

import io.jsonwebtoken.Claims;

@Slf4j
@Component
@RequiredArgsConstructor
public class LiveBoardInterceptor implements ChannelInterceptor {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(
                        message, StompHeaderAccessor.class); // stomp 메세지의 헤더를 분석 ( 커멘드, 세션아이디 등등..)
        StompCommand command = accessor.getCommand();
        log.info("🔐 [LiveBoardInterceptor] Command: {}", accessor.getCommand());
        log.info("🔐 [LiveBoardInterceptor] User: {}", accessor.getUser());

        if (StompCommand.CONNECT.equals(command)) {
            // TODO : 글로벌 세션아이디를 만들어서 넘겨주기
            String token = accessor.getFirstNativeHeader("Authorization");

            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
                Claims claims = jwtUtil.validateToken(token);

                if (claims != null) { // 토큰이 있을때
                    String email = claims.getSubject();

                    CustomUserPrincipal userDetails = userDetailsService.loadUserByUsername(email);
                    User user = userDetails.getUser();
                    Long userId = user.getId();
                    String nickname = user.getNickname();

                    // 웹소켓에 사용자 등록
                    StompPrincipal principal = new StompPrincipal(userId, nickname);
                    accessor.setUser(principal);

                    // Spring 시큐리티 인증
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    String rawSessionId = accessor.getSessionId();
                    String globalSessionId = GlobalSessionIdGenerator.generate(rawSessionId);
                    accessor.setNativeHeader("GLOBAL_SESSION_HEADER", globalSessionId);
                }
            }
        }
        return message;
    }
}
