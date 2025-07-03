package com.sparta.spartatigers.domain.alarm.scheduler;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import com.sparta.spartatigers.domain.alarm.dto.response.AlarmInfo;
import com.sparta.spartatigers.domain.alarm.model.entity.Alarm;
import com.sparta.spartatigers.domain.alarm.redis.RedisAlarmPublisher;
import com.sparta.spartatigers.domain.alarm.repository.AlarmRepository;

import com.fasterxml.jackson.databind.ObjectMapper;

@Log4j2
@Component
@RequiredArgsConstructor
public class AlarmScheduler {
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final RedisAlarmPublisher redisAlarmPublisher;
    private final AlarmRepository alarmRepository;
    private final RedissonClient redissonClient;
    private static final String REDIS_KEY = "alarms";
    private static final String LOCK_KEY = "lock:alarmScheduler";

    @Scheduled(fixedRate = 5_000)
    @Transactional
    public void sendAlarms() {
        System.out.println("알람 스케줄러 실행@@@@@");
        RLock lock = redissonClient.getLock(LOCK_KEY);

        boolean isLocked = false;
        try {
            isLocked = lock.tryLock(1, 10, TimeUnit.SECONDS);

            if (!isLocked) {
                return;
            }

            ZoneOffset offset = ZoneOffset.of("+09:00");
            long now = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES).toEpochSecond(offset);
            long fiveMinutesAgo = now - 300;

            Set<String> alarmJsons =
                    redisTemplate.opsForZSet().rangeByScore(REDIS_KEY, fiveMinutesAgo, now);
            if (alarmJsons == null || alarmJsons.isEmpty()) return;

            for (String alarmJson : alarmJsons) {
                try {
                    String innerJson =
                            objectMapper.readValue(alarmJson, String.class); // JSON 문자열 추출
                    AlarmInfo alarmInfo = objectMapper.readValue(innerJson, AlarmInfo.class);
                    redisAlarmPublisher.publishAlarm(alarmInfo);
                    redisTemplate.opsForZSet().remove(REDIS_KEY, alarmJson);

                    Alarm alarm = alarmRepository.findById(alarmInfo.getAlarmId()).orElse(null);

                    if (alarm != null) {
                        LocalDateTime currentAlarmTime = alarmInfo.getAlarmTime();
                        boolean updated = false;

                        if (alarm.getNormalAlarmTime() != null
                                && alarm.getNormalAlarmTime().equals(currentAlarmTime)) {
                            alarm.updateNormalAlarmTime(null);
                            updated = true;
                        }

                        if (alarm.getPreAlarmTime() != null
                                && alarm.getPreAlarmTime().equals(currentAlarmTime)) {
                            alarm.updatePreAlarmTime(null);
                            updated = true;
                        }

                        if (updated) {
                            if (alarm.getNormalAlarmTime() == null
                                    && alarm.getPreAlarmTime() == null) {
                                alarmRepository.delete(alarm);
                            } else {
                                alarmRepository.save(alarm);
                            }
                        }
                    }

                } catch (Exception e) {
                    log.error("Failed to process alarm: {}", alarmJson, e);
                }
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            if (isLocked) {
                lock.unlock();
            }
        }
    }
}
