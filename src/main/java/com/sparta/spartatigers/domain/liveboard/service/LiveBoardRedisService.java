package com.sparta.spartatigers.domain.liveboard.service;

import java.time.LocalDateTime;
import java.util.Map;
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
import com.sparta.spartatigers.domain.liveboard.repository.LiveBoardConnectionRepository;
import com.sparta.spartatigers.domain.liveboard.util.GlobalSessionIdGenerator;
import com.sparta.spartatigers.domain.user.model.CustomUserPrincipal;
import com.sparta.spartatigers.domain.user.model.entity.User;
import com.sparta.spartatigers.domain.user.repository.UserRepository;
import com.sparta.spartatigers.global.exception.ExceptionCode;
import com.sparta.spartatigers.global.exception.InvalidRequestException;

import com.fasterxml.jackson.databind.ObjectMapper;

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

    private ChannelTopic getOrInitTopic(String roomId) {
        ChannelTopic topic = topics.get(roomId);
        if (topic == null) {
            topic = new ChannelTopic(roomId);
            RedisMessageSubscriber subscriber =
                    new RedisMessageSubscriber(objectMapper, redisTemplate, messagingTemplate);
            redisMessageListener.addMessageListener(subscriber, topic);
            topics.put(roomId, topic);
        }
        return topic;
    }

    private Long findSenderId(Authentication authentication) {
        Object principalObj = authentication.getPrincipal();

        if (principalObj instanceof CustomUserPrincipal principal) {
            return principal.getUser().getId();
        } else if (principalObj instanceof StompPrincipal principal) {
            return Long.parseLong(principal.getName());
        }
        throw new RuntimeException("지원하지 않는 principal타입 : " + principalObj.getClass());
    }

    private String generateGlobalSessionId(Message<LiveBoardMessage> message) {
        SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessor.wrap(message);
        String sessionId = accessor.getSessionId();
        return GlobalSessionIdGenerator.generate(sessionId);
    }

    // 채팅 전송
    public void handleMessage(LiveBoardMessage message, Authentication authentication) {
        Object principalObj = authentication.getPrincipal();
        if (principalObj == null
                || principalObj instanceof StompPrincipal principal
                        && principal.getName().equals("null")) {
            throw new RuntimeException("비회원 사용자는 라이브보드 메세지를 보낼 수 없습니다.");
        }

        Long senderId = findSenderId(authentication);

        // 메세지에 유저정보 세팅
        User sender =
                userRepository
                        .findById(senderId)
                        .orElseThrow(
                                () -> new InvalidRequestException(ExceptionCode.USER_NOT_FOUND));
        String nickname = sender.getNickname();
        message =
                LiveBoardMessage.of(message.getRoomId(), senderId, nickname, message.getContent());

        // Redis publish
        ChannelTopic topic = getOrInitTopic(message.getRoomId());
        redisPublisher.publish(topic, message);
    }

    // 입장
    public void enterRoom(Message<LiveBoardMessage> message, Authentication authentication) {
        Object principalObj = authentication.getPrincipal();

        if (principalObj == null) {
            log.warn("비회원 유저가 입장 시도");
            return;
        }

        Long senderId = findSenderId(authentication);
        String globalSessionId = generateGlobalSessionId(message);

        // 룸토픽 던져
        String roomId = message.getPayload().getRoomId();
        ChannelTopic topic = getOrInitTopic(roomId);

        // 커넥션 객체 생성
        LiveBoardConnection connection =
                LiveBoardConnection.of(
                        globalSessionId,
                        senderId != null ? String.valueOf(senderId) : null,
                        roomId,
                        LocalDateTime.now());
        liveBoardConnectionRepository.saveConnection(roomId, globalSessionId, connection);

        redisPublisher.publish(topic, message.getPayload());
    }

    // 퇴장
    public void exitRoom(Message<LiveBoardMessage> message) {
        String roomId = message.getPayload().getRoomId();
        String globalSessionId = generateGlobalSessionId(message);
        liveBoardConnectionRepository.deleteConnection(roomId, globalSessionId);

        // 룸토픽 던져
        ChannelTopic topic = getOrInitTopic(roomId);
        redisPublisher.publish(topic, message.getPayload());
    }
}
