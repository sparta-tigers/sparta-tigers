package com.sparta.spartatigers.domain.alarm.redis;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import com.sparta.spartatigers.domain.alarm.dto.response.AlarmInfo;
import com.sparta.spartatigers.domain.alarm.service.AlarmService;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
public class RedisAlarmSubscriber implements MessageListener {
    private final AlarmService alarmService;
    private final ObjectMapper objectMapper;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String body = new String(message.getBody());
            String jsonStr = objectMapper.readValue(body, String.class);
            AlarmInfo info = objectMapper.readValue(jsonStr, AlarmInfo.class);
            alarmService.sendAlarm(info);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
