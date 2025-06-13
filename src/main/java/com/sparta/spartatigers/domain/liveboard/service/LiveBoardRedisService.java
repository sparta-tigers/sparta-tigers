package com.sparta.spartatigers.domain.liveboard.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.sparta.spartatigers.domain.liveboard.model.LiveBoardMessage;
import com.sparta.spartatigers.domain.liveboard.pubsub.RedisMessagePublisher;
import com.sparta.spartatigers.domain.liveboard.pubsub.RedisMessageSubscriber;

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

    // stomp 메세지 전송
    public void handleMessage(LiveBoardMessage message) {
        String roomId = message.getRoomId();
        ChannelTopic topic = getTopic(roomId);
        if (topic == null) {
            initRoomTopic(roomId, messagingTemplate);
            topic = getTopic(roomId);
        }
        redisPublisher.publish(topic, message);
    }
}
