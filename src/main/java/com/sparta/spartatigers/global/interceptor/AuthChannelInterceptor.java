// package com.sparta.spartatigers.global.interceptor;
//
// import java.security.Principal;
// import java.util.Map;
//
// import org.springframework.messaging.Message;
// import org.springframework.messaging.MessageChannel;
// import org.springframework.messaging.simp.stomp.StompCommand;
// import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
// import org.springframework.messaging.support.ChannelInterceptor;
// import org.springframework.messaging.support.MessageHeaderAccessor;
// import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
// import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
// import org.springframework.stereotype.Component;
//
// import lombok.extern.slf4j.Slf4j;
//
// import com.sparta.spartatigers.domain.user.model.CustomUserPrincipal;
//
// @Slf4j
// @Component
// public class AuthChannelInterceptor implements ChannelInterceptor {
//
//     @Override
//     public Message<?> preSend(Message<?> message, MessageChannel channel) {
//         StompHeaderAccessor accessor =
//                 MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
//
//         if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
//             Principal principal = accessor.getUser();
//
//             if (principal == null) {
//                 log.warn("WebSocket CONNECT 시점에 Principal이 존재하지 않습니다.");
//                 return message;
//             }
//
//             CustomUserPrincipal customUserPrincipal = null;
//
//             // 1. Authentication 객체에서 Principal을 꺼냅니다.
//             if (principal instanceof UsernamePasswordAuthenticationToken authToken) {
//                 if (authToken.getPrincipal() instanceof CustomUserPrincipal) {
//                     customUserPrincipal = (CustomUserPrincipal) authToken.getPrincipal();
//                 }
//             } else if (principal instanceof OAuth2AuthenticationToken oauthToken) {
//                 if (oauthToken.getPrincipal() instanceof CustomUserPrincipal) {
//                     customUserPrincipal = (CustomUserPrincipal) oauthToken.getPrincipal();
//                 }
//             }
//
//             if (customUserPrincipal != null) {
//                 Long userId = customUserPrincipal.getUser().getId(); // 내부 ID를 사용!
//
//                 Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
//                 if (sessionAttributes != null) {
//                     sessionAttributes.put("userId", userId);
//                     log.info("✅ WebSocket 세션에 userId 저장됨: {}", userId);
//                 }
//             } else {
//                 log.warn(
//                         "Principal 객체에서 CustomUserPrincipal을 추출하지 못했습니다: {}",
//                         principal.getClass().getName());
//             }
//         }
//         return message;
//     }
// }
