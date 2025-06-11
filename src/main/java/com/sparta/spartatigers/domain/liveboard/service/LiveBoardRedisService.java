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

import com.sparta.spartatigers.domain.liveboard.pubsub.RedisMessageSubscriber;
import com.sparta.spartatigers.domain.liveboard.repository.LiveBoardRoomRepository;

import com.fasterxml.jackson.databind.ObjectMapper;

@Slf4j
@Service
@RequiredArgsConstructor
public class LiveBoardRedisService {

    private final RedisMessageListenerContainer redisMessageListener;
    private final ObjectMapper objectMapper;
    private final RedisTemplate<String, String> redisTemplate;

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

    // 입장할 때 호출하면 됨
    public void enterRoom(String roomId, SimpMessageSendingOperations messagingTemplate) {
        registerRoomTopic(roomId, messagingTemplate);
    }

    // 채널 토픽 반환 (publish시 사용)
    public ChannelTopic getTopic(String roomId) {
        return topics.get(roomId);
    }

}
