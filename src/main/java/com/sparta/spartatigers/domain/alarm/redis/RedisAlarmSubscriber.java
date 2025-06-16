package com.sparta.spartatigers.domain.alarm.redis;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import com.sparta.spartatigers.domain.alarm.dto.response.AlarmInfo;
import com.sparta.spartatigers.domain.alarm.service.AlarmServiceImpl;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
public class RedisAlarmSubscriber implements MessageListener {
    private final AlarmServiceImpl alarmService;
    private final ObjectMapper objectMapper;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String body = new String(message.getBody());
            // 1단계: body가 JSON 문자열이 또 감싸져 있을 수 있으니 한번 String으로 변환
            System.out.println("message body: " + body);

            String jsonStr = objectMapper.readValue(body, String.class);
            // 2단계: 다시 AlarmInfo 객체로 변환
            AlarmInfo info = objectMapper.readValue(jsonStr, AlarmInfo.class);
            alarmService.sendAlarm(info);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
