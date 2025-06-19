package com.sparta.spartatigers.domain.liveboard.service;

import java.time.LocalDateTime;
import java.util.List;
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
import com.sparta.spartatigers.domain.liveboard.model.MessageType;
import com.sparta.spartatigers.domain.liveboard.pubsub.RedisMessagePublisher;
import com.sparta.spartatigers.domain.liveboard.pubsub.RedisMessageSubscriber;
import com.sparta.spartatigers.domain.liveboard.repository.LiveBoardConnectionRepository;
import com.sparta.spartatigers.domain.user.model.CustomUserPrincipal;
import com.sparta.spartatigers.domain.user.repository.UserRepository;

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
        return topics.computeIfAbsent(
                roomId,
                key -> {
                    ChannelTopic topic = new ChannelTopic(key);
                    RedisMessageSubscriber subscriber =
                            new RedisMessageSubscriber(
                                    objectMapper, redisTemplate, messagingTemplate);
                    redisMessageListener.addMessageListener(subscriber, topic);
                    return topic;
                });
    }

    private Long findSenderId(Authentication authentication) {
        Object principalObj = authentication.getPrincipal();

        if (principalObj instanceof CustomUserPrincipal principal) {
            return principal.getUser().getId();
        } else if (principalObj instanceof StompPrincipal principal) {
            return Long.parseLong(principal.getName());
        }
        return null;
    }

    private String getGlobalSessionId(Message<LiveBoardMessage> message) {
        SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessor.wrap(message);
        String globalSessionId = accessor.getSessionId();
        return globalSessionId;
    }

    // 채팅 전송
    public void handleMessage(LiveBoardMessage message, Authentication authentication) {
        Object principalObj = authentication.getPrincipal();

        if (principalObj == null
                || principalObj instanceof StompPrincipal principal
                        && "null".equals(principal.getName())) {
            throw new RuntimeException("비회원 사용자는 라이브보드 메세지를 보낼 수 없습니다.");
        }

        // 메세지에 유저정보 세팅
        Long senderId = findSenderId(authentication);
        String nickname = userRepository.findNicknameById(senderId).orElse("비회원");
        message =
                LiveBoardMessage.of(
                        message.getRoomId(),
                        senderId,
                        nickname,
                        message.getContent(),
                        MessageType.CHAT);

        // Redis publish
        ChannelTopic topic = getOrInitTopic(message.getRoomId());
        redisPublisher.publish(topic, message);
    }

    // 입장 TODO: Message<LiveBoardMessage> message -> 글로벌 세션ID만 뺄수있음 된다!, paylood 없어도 된다
    public void enterRoom(Message<LiveBoardMessage> message, Authentication authentication) {
        Long senderId = findSenderId(authentication);
        String nickname =
                senderId != null ? userRepository.findNicknameById(senderId).orElse("비회원") : "비회원";
        String globalSessionId = getGlobalSessionId(message);

        // get topic
        String roomId = message.getPayload().getRoomId();
        ChannelTopic topic = getOrInitTopic(roomId);

        LiveBoardMessage enterMessage =
                LiveBoardMessage.of(roomId, senderId, nickname, "입장", MessageType.ENTER);

        // connection 생성 후 저장
        LiveBoardConnection connection =
                LiveBoardConnection.of(
                        globalSessionId, senderId, nickname, roomId, LocalDateTime.now());
        liveBoardConnectionRepository.saveConnection(roomId, globalSessionId, connection);

        redisPublisher.publish(topic, enterMessage);
    }

    // 퇴장 TODO: 글로벌 세션id 새로 생성 X / 위랑 똑같이 이미 만들어진 글로벌세션 ID 받아오기
    public void exitRoom(Message<LiveBoardMessage> message) {
        // connection 삭제
        String roomId = message.getPayload().getRoomId();
        String globalSessionId = getGlobalSessionId(message);
        liveBoardConnectionRepository.deleteConnection(roomId, globalSessionId);

        LiveBoardMessage exitMessage =
                LiveBoardMessage.of(roomId, null, "유저", "퇴장", MessageType.EXIT);

        // get topic 후 publish
        ChannelTopic topic = getOrInitTopic(roomId);
        redisPublisher.publish(topic, exitMessage);
    }

    public void handleDisconnect(String globalSessionId) {
        List<String> roomIds = liveBoardConnectionRepository.findAllRoomIds();

        for (String roomId : roomIds) {
            Map<Object, Object> connections =
                    liveBoardConnectionRepository.findAllConnections(roomId);

            if (connections.containsKey(globalSessionId)) {
                liveBoardConnectionRepository.deleteConnection(roomId, globalSessionId);
            }
        }
    }
}
