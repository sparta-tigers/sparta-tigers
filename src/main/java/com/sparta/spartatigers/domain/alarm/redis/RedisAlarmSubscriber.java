package com.sparta.spartatigers.domain.alarm.redis;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import com.sparta.spartatigers.domain.alarm.dto.response.AlarmInfo;
import com.sparta.spartatigers.domain.alarm.service.AlarmService;

import com.fasterxml.jackson.databind.ObjectMapper;

@Log4j2
@Component
@RequiredArgsConstructor
public class RedisAlarmSubscriber implements MessageListener {
    private final AlarmService alarmService;
    private final ObjectMapper objectMapper;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String jsonStr = new String(message.getBody());
            log.info("TEST 시작");
            log.info(jsonStr);
            String innerJson = objectMapper.readValue(jsonStr, String.class);
            log.info(innerJson);
            AlarmInfo info = objectMapper.readValue(innerJson, AlarmInfo.class);
            log.info(info);
            alarmService.sendAlarm(info);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
