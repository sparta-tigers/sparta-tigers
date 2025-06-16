package com.sparta.spartatigers.domain.item.pubsub;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import com.sparta.spartatigers.domain.item.dto.response.RedisUpdateDto;

@Component
@RequiredArgsConstructor
public class LocationPublisher {

    private final RedisTemplate<String, Object> redisTemplate;

    public void publishLocation(RedisUpdateDto dto) {
        redisTemplate.convertAndSend("location-channel", dto);
    }
}
