package com.sparta.spartatigers.global.config;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.spartatigers.domain.liveboard.model.LiveBoardMessage;
import com.sparta.spartatigers.domain.liveboard.model.LiveBoardUser;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WebSocketHandler extends TextWebSocketHandler {

	private final Map<String, LiveBoardUser> user = new ConcurrentHashMap<>();
	private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
	private static final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {

		// 사용자 저장
		LiveBoardUser sessionUser = LiveBoardUser.builder()
			.sessionId(session.getId())
			.userId("") // 유저 식별자 필요, 비회원은 null TODO: 카톡 로그인 유저 식별자 꺼내는 메서드 필요
			.nickname("비회원"+ UUID.randomUUID().toString().substring(0, 5)) // TODO: 익명 닉네임 랜덤 생성하기~!
			.isMember(false)
			.connectedAt(LocalDateTime.now())
			.roomId("1") // TODO: 수정
			.build();

		// map에 저장
		user.put(session.getId(), sessionUser);
		sessions.put(session.getId(), session);

		// 전체 접속자 수 카운트

	};

	@Override
	public void handleTextMessage(WebSocketSession session, TextMessage textMessage) throws Exception{
		// 역직렬화
		LiveBoardMessage message = objectMapper.readValue(textMessage.getPayload(), LiveBoardMessage.class);
		// 직렬화
		String json = objectMapper.writeValueAsString(message);

		// 유저 정보 가져오기


		// 현재 사용자와 roomId가 같은 유저들 찾기
		String senderSessionId = session.getId();
		String roomId = user.get(senderSessionId).getRoomId();

		// 접속중인 모든 유저에게 브로드캐스트
		for (Map.Entry<String, LiveBoardUser> all : user.entrySet()) {
			if (roomId.equals(all.getValue().getRoomId())) {
				WebSocketSession targetSession = this.sessions.get(all.getKey());
				if (targetSession.isOpen()) {
					targetSession.sendMessage(new TextMessage(json));
				}
			}
		}
	};

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
		String sessionId = session.getId();
		user.remove(sessionId);

		// 전체 접속자 수 카운트

	};

	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception) {};
}
