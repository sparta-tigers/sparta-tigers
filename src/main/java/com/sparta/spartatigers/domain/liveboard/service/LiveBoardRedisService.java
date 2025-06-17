package com.sparta.spartatigers.domain.liveboard.service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.sparta.spartatigers.domain.chatroom.config.StompPrincipal;
import com.sparta.spartatigers.domain.liveboard.model.LiveBoardMessage;
import com.sparta.spartatigers.domain.liveboard.pubsub.RedisMessagePublisher;
import com.sparta.spartatigers.domain.liveboard.pubsub.RedisMessageSubscriber;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.spartatigers.domain.user.model.CustomUserPrincipal;

@Slf4j
@Service
@RequiredArgsConstructor
public class LiveBoardRedisService {

    private final RedisMessageListenerContainer redisMessageListener;
    private final ObjectMapper objectMapper;
    private final RedisTemplate<String, String> redisTemplate;
    private final SimpMessageSendingOperations messagingTemplate;
    private final RedisMessagePublisher redisPublisher;

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
        Object pricipalObj = authentication.getPrincipal();
		Long senderId;

		// 예외 - 1. principal이 null일때, 2. stompprincipal의 userid가 null일때
		if(pricipalObj == null) {
			throw new RuntimeException("비회원 사용자는 라이브보드 메세지를 보낼 수 없습니다.");
		} else if (pricipalObj instanceof StompPrincipal principal) {
			String userId = principal.getName();
			if (userId == null || userId.equals("null")) {
				throw new RuntimeException("비회원 사용자는 라이브보드 메세지를 보낼 수 없습니다.");
			}
			senderId = Long.parseLong(userId);
		} else if (pricipalObj instanceof CustomUserPrincipal principal) {
			senderId = principal.getUser().getId();
		} else {
			throw new RuntimeException("지원하지 않는 principal타입 : " + pricipalObj.getClass());
		}

		// 메세지에 senderId 세팅
		message = LiveBoardMessage.of(message.getRoomId(), senderId, message.getContent());

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
	public void enterRoom(LiveBoardMessage message, Authentication authentication) {
		String roomId = message.getRoomId();
		ChannelTopic topic = getTopic(roomId);
		if (topic == null) {
			initRoomTopic(roomId, messagingTemplate);
			topic = getTopic(roomId);
		}
		redisPublisher.publish(topic, message);
	}

	// 퇴장
	public void exitRoom(LiveBoardMessage message, Authentication authentication) {
		String roomId = message.getRoomId();
		ChannelTopic topic = getTopic(roomId);
		if (topic == null) {
			initRoomTopic(roomId, messagingTemplate);
			topic = getTopic(roomId);
		}
		redisPublisher.publish(topic, message);
	}
}
