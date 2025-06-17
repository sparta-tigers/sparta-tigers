package com.sparta.spartatigers.domain.liveboard.service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.sparta.spartatigers.domain.chatroom.config.StompPrincipal;
import com.sparta.spartatigers.domain.liveboard.model.LiveBoardConnection;
import com.sparta.spartatigers.domain.liveboard.model.LiveBoardMessage;
import com.sparta.spartatigers.domain.liveboard.pubsub.RedisMessagePublisher;
import com.sparta.spartatigers.domain.liveboard.pubsub.RedisMessageSubscriber;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.spartatigers.domain.liveboard.repository.LiveBoardConnectionRepository;
import com.sparta.spartatigers.domain.liveboard.util.GlobalSessionIdGenerator;
import com.sparta.spartatigers.domain.user.model.CustomUserPrincipal;
import com.sparta.spartatigers.domain.user.model.entity.User;
import com.sparta.spartatigers.domain.user.repository.UserRepository;
import com.sparta.spartatigers.global.exception.ExceptionCode;
import com.sparta.spartatigers.global.exception.InvalidRequestException;

@Slf4j
@Service
@RequiredArgsConstructor
public class LiveBoardRedisService {

    private final RedisMessageListenerContainer redisMessageListener;
    private final ObjectMapper objectMapper;
    private final RedisTemplate<String, String> redisTemplate;
    private final SimpMessageSendingOperations messagingTemplate;
    private final RedisMessagePublisher redisPublisher;
	private final UserRepository userRepository;
	private final LiveBoardConnectionRepository liveBoardConnectionRepository;

	private Map<String, ChannelTopic> topics = new ConcurrentHashMap<>(); // 채팅방별 topic을 roomId로 찾기

    public void registerRoomTopic(String roomId, SimpMessageSendingOperations messagingTemplate) {
        if (!topics.containsKey(roomId)) {
            ChannelTopic topic = new ChannelTopic(roomId);
            RedisMessageSubscriber subscriber =
                    new RedisMessageSubscriber(objectMapper, redisTemplate, messagingTemplate);
            redisMessageListener.addMessageListener(subscriber, topic);
            topics.put(roomId, topic);
        }
    }

    public void initRoomTopic(String roomId, SimpMessageSendingOperations messagingTemplate) {
        registerRoomTopic(roomId, messagingTemplate);
    }

    // 채널 토픽 반환 (publish시 사용)
    public ChannelTopic getTopic(String roomId) {
        return topics.get(roomId);
    }

    // 메세지 전송
    public void handleMessage(LiveBoardMessage message, Authentication authentication) {
        Object principalObj = authentication.getPrincipal();
		Long senderId;


		if(principalObj == null || principalObj instanceof StompPrincipal principal && principal.getName().equals("null")) {
			throw new RuntimeException("비회원 사용자는 라이브보드 메세지를 보낼 수 없습니다.");
		}


		if (principalObj instanceof CustomUserPrincipal principal) {
			senderId = principal.getUser().getId();
		} else if (principalObj instanceof StompPrincipal principal) {
			senderId = Long.parseLong(principal.getName());
		} else {
			throw new RuntimeException("지원하지 않는 principal타입 : " + principalObj.getClass());
		}

		// 메세지에 유저정보 세팅
		User sender =
			userRepository
				.findById(senderId)
				.orElseThrow(
					() ->
						new InvalidRequestException(
							ExceptionCode.USER_NOT_FOUND));
		String nickname = sender.getNickname();
		message = LiveBoardMessage.of(message.getRoomId(), senderId, nickname, message.getContent());

		// Redis publish
		String roomId = message.getRoomId();
        ChannelTopic topic = getTopic(roomId);
        if (topic == null) {
            initRoomTopic(roomId, messagingTemplate);
            topic = getTopic(roomId);
        }
        redisPublisher.publish(topic, message);
    }

	// 입장
	public void enterRoom(Message<LiveBoardMessage> message, Authentication authentication) {
		// 유저 Id, 닉네임 찾으셈
		Object principalObj = authentication.getPrincipal();
		Long senderId = null;

		if(principalObj instanceof CustomUserPrincipal principal) {
			senderId = principal.getUser().getId();
		} else if (principalObj instanceof StompPrincipal principal) {
			senderId = Long.parseLong(principal.getName());
		}

		// 헤더의 세션 Id 찾으셈
		SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessor.wrap(message);
		String sessionId = accessor.getSessionId();
		String globalSessionId = GlobalSessionIdGenerator.generate(sessionId);

		// 룸토픽 던져
		String roomId = message.getPayload().getRoomId();
		ChannelTopic topic = getTopic(roomId);
		if (topic == null) {
			initRoomTopic(roomId, messagingTemplate);
			topic = getTopic(roomId);
		}

		// 커넥션 객체 생성
		LiveBoardConnection connection = LiveBoardConnection.of(
			globalSessionId,
			senderId != null ? String.valueOf(senderId) : null,
			roomId,
			LocalDateTime.now()
		);
		liveBoardConnectionRepository.saveConnection(roomId, globalSessionId, connection);
		redisPublisher.publish(topic, message.getPayload());
	}

	// 퇴장
	public void exitRoom(Message<LiveBoardMessage> message, Authentication authentication) {

		// 헤더의 세션 Id 찾으셈
		SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessor.wrap(message);
		String sessionId = accessor.getSessionId();
		String globalSessionId = GlobalSessionIdGenerator.generate(sessionId);

		// 룸토픽 던져
		String roomId = message.getPayload().getRoomId();
		ChannelTopic topic = getTopic(roomId);
		if (topic == null) {
			initRoomTopic(roomId, messagingTemplate);
			topic = getTopic(roomId);
		}

		liveBoardConnectionRepository.deleteConnection(roomId, globalSessionId);
		redisPublisher.publish(topic, message.getPayload());
	}
}
