package com.sparta.spartatigers.domain.liveboard.interceptor;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import com.sparta.spartatigers.domain.liveboard.service.LiveBoardRoomService;

@Component
@RequiredArgsConstructor
public class LiveBoardInterceptor implements ChannelInterceptor {

    private final LiveBoardRoomService liveBoardRoomService;
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor =
                StompHeaderAccessor.wrap(message); // stomp 메세지의 헤더를 분석 ( 커멘드, 세션아이디 등등..)
        StompCommand command = accessor.getCommand();

		// 채팅방 구독시
        if (StompCommand.SUBSCRIBE.equals(command)) {
            String destination = accessor.getDestination(); // url
            String sessionId = accessor.getSessionId(); // 세션 id

            if (destination != null && destination.startsWith("/server/liveboard/room/")) {
                String enterRoomId = destination.substring("/server/liveboard/room/".length());
                String lastRoomId = redisTemplate.opsForValue().get(sessionId);

                // 다른 채팅방에서 넘어온 경우
                if (lastRoomId != null && !lastRoomId.equals(enterRoomId)) {
                    liveBoardRoomService.decreaseConnectCount(lastRoomId);
                }

                // 새로 입장한 채팅방 접속자 수 증가
                redisTemplate.opsForValue().set(sessionId, enterRoomId);
            }
        }

        // 웹소켓 연결 종료시
        if (StompCommand.DISCONNECT.equals(command)) {
            String sessionId = accessor.getSessionId();
            String roomId = redisTemplate.opsForValue().get(sessionId);

            if (roomId != null) {
                redisTemplate.delete(sessionId);
            }
        }
        return message;
    }
}
