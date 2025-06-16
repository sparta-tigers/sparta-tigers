package com.sparta.spartatigers.domain.alarm.redis;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import com.sparta.spartatigers.domain.alarm.dto.response.AlarmInfo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RequiredArgsConstructor
@Component
public class RedisAlarmPublisher {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public void publishAlarm(AlarmInfo alarmInfo) {
        try {
            String json = objectMapper.writeValueAsString(alarmInfo);
            redisTemplate.convertAndSend("alarmChannel", json);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
