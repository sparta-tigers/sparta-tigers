package com.sparta.spartatigers.global.handler;

import java.security.Principal;
import java.util.Map;

import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

public class DefaultWebSocketHandshakeHandler extends DefaultHandshakeHandler {

    @Override
    protected Principal determineUser(
            org.springframework.http.server.ServerHttpRequest request,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes) {

        // attributes에서 userId 꺼내기
        String userId = (String) attributes.get("userId");

        // Principal 객체 생성
        return new Principal() {
            @Override
            public String getName() {
                return userId;
            }
        };
    }
}
