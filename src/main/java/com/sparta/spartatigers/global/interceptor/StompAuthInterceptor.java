package com.sparta.spartatigers.global.interceptor;

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
import lombok.extern.slf4j.Slf4j;

import com.sparta.spartatigers.domain.chatroom.model.security.StompPrincipal;
import com.sparta.spartatigers.domain.chatroom.registry.RedisUserSessionRegistry;
import com.sparta.spartatigers.domain.user.model.entity.User;
import com.sparta.spartatigers.domain.user.service.CustomUserDetailsService;
import com.sparta.spartatigers.global.exception.ExceptionCode;
import com.sparta.spartatigers.global.exception.InvalidRequestException;
import com.sparta.spartatigers.global.util.JwtUtil;

import io.jsonwebtoken.Claims;

@Component
@RequiredArgsConstructor
@Slf4j
public class StompAuthInterceptor implements ChannelInterceptor {

    private static final String CHAT_DOMAIN_HEADER = "ChatDomain";
    private static final String CHAT_DOMAIN_DIRECTROOM = "directroom";
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;
    private final RedisUserSessionRegistry userSessionRegistry;

    /**
     * 연결할 때 JWT 토큰을 검증하고, 인증된 사용자 정보를 SecurityContext와 StompHeaderAccessor에 설정,
     * RedisUserSessionRegistry에 세션-사용자 매핑 정보를 저장
     *
     * @param message STOMP 메시지
     * @param channel 메시지 채널
     * @return 인증된 메시지
     * @throws InvalidRequestException 토큰이 없거나 유효하지 않은 경우 예외 발생
     */
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        // 첫 연결할 때만 인증 및 세션id-userid 레지스트리에 저장 수행
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String chatDomain = accessor.getFirstNativeHeader(CHAT_DOMAIN_HEADER);

            // StompAuthInterceptor를 채팅 기능에만 동작하도록 수정
            if (chatDomain == null ||
!CHAT_DOMAIN_DIRECTROOM.equalsIgnoreCase(chatDomain.trim())) {
                log.info("인증 생략: ChatDomain = {}", chatDomain);
                accessor.setUser(null);
                SecurityContextHolder.clearContext();
                return message;
            }

            String token = accessor.getFirstNativeHeader("Authorization");

            log.info("Authorization header: {}", token);

            if (token == null || !token.startsWith("Bearer ")) {
                throw new InvalidRequestException(ExceptionCode.NOT_FOUND_JWT);
            }
            token = token.substring(7);

            Claims claims = jwtUtil.validateToken(token);
            if (claims == null) {
                throw new InvalidRequestException(ExceptionCode.NOT_FOUND_JWT);
            }

            // 토큰에서 현재 유저의 이메일 추출 후 사용자 정보 조회
            String email = claims.getSubject();
            User user = userDetailsService.loadUserByUsername(email).getUser();

            // 인증된 사용자 정보로 StompPrincipal 생성 및 메시지에 등록
            StompPrincipal principal = new StompPrincipal(user.getId(), user.getNickname());
            accessor.setUser(principal);

            // Spring Security 인증 토큰 생성 및 SecurityContext에 저장
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            principal, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 세션-유저 매핑 저장소에 등록
            userSessionRegistry.registerSession(user.getId(), accessor.getSessionId());

            log.info("연결된 userId: {}", user.getId());
            log.info("세션 ID: {}", accessor.getSessionId());
        }

        return message;
    }
}
