package com.sparta.spartatigers.global.handler;

import jakarta.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

/**
 * 1. 이 친구가 WebSocketConnection HandShake 할때 도는 친구 2. 여기서 만약에 인증 정보가 있으면, 인증 정보를 기반으로 Principal 3.
 * 인증 정보가 없으면 UUID 반환 4. SeucirtyContext에 인증정보가 있으면 5. 시큐리티가 들어왔을 때, 인증정보를 우리가 넣어야 하는지? 인터셉터 TODO
 * 살펴볼 필요가 있음
 */
public class DefaultWebSocketHandshakeHandler extends DefaultHandshakeHandler {

	@Override
	protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler,
		Map<String, Object> attributes) {

		if (request instanceof ServletServerHttpRequest serverHttpRequest) {
			HttpServletRequest servletRequest = serverHttpRequest.getServletRequest();
			// TODO Security 인증이 도입되었을 때 별도의 인터셉터를 통해서 수동으로 인증 객체를 만들어 줘야할 가능성이 있음.
			// Spring Security라면 여기서 Security에 의해 세팅된 유저 정보가 나온다고한다.
			Principal userPrincipal = servletRequest.getUserPrincipal();
			if (userPrincipal != null) {
				// 회원인 경우
				return userPrincipal;
			}
		}

		String sessionId = UUID.randomUUID().toString();
		return () -> sessionId;
	}
}
