package com.sparta.spartatigers.domain.liveboard.pubsub;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import com.sparta.spartatigers.domain.liveboard.model.LiveBoardMessage;

@RequiredArgsConstructor
@Service
public class RedisMessagePublisher {

    private final RedisTemplate<String, Object> redisTemplate;

    public void publish(ChannelTopic topic, LiveBoardMessage message) {
        redisTemplate.convertAndSend(topic.getTopic(), message);
    }
}
