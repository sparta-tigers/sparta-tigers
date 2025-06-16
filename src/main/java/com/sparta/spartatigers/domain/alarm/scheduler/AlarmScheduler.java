package com.sparta.spartatigers.domain.alarm.scheduler;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Set;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import com.sparta.spartatigers.domain.alarm.dto.response.AlarmInfo;
import com.sparta.spartatigers.domain.alarm.redis.RedisAlarmPublisher;
import com.sparta.spartatigers.domain.alarm.service.AlarmService;

import com.fasterxml.jackson.databind.ObjectMapper;

@Log4j2
@Component
@RequiredArgsConstructor
public class AlarmScheduler {
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final RedisAlarmPublisher redisAlarmPublisher;
    private final AlarmService alarmService;
    private static final String REDIS_KEY = "alarms";

    @Scheduled(fixedRate = 60_000)
    public void sendAlarms() {
        long now =
                LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES).toEpochSecond(ZoneOffset.UTC);
        log.info("Scheduler running at {}", now);

        Set<String> alarmJsons = redisTemplate.opsForZSet().rangeByScore(REDIS_KEY, now, now);
        if (alarmJsons == null || alarmJsons.isEmpty()) return;

        for (String alarmJson : alarmJsons) {
            try {
                String innerJson = objectMapper.readValue(alarmJson, String.class);
                AlarmInfo alarm = objectMapper.readValue(innerJson, AlarmInfo.class);
                redisAlarmPublisher.publishAlarm(alarm);
                redisTemplate.opsForZSet().remove(REDIS_KEY, alarmJson);
                alarmService.deleteAlarm(alarm.getUserId(), alarm.getMatchId());
            } catch (Exception e) {
                log.error("Failed to process alarm: {}", alarmJson, e);
            }
        }
    }
}
