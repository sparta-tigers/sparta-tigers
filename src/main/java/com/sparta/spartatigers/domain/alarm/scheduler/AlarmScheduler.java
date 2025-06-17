package com.sparta.spartatigers.domain.alarm.scheduler;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Set;

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
    private static final String REDIS_KEY = "alarms";

    @Scheduled(fixedRate = 60_000)
    @Transactional
    public void sendAlarms() {
        ZoneOffset offset = ZoneOffset.of("+09:00");
        long now = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES).toEpochSecond(offset);
        long fiveMinutesAgo = now - 300;

        Set<String> alarmJsons =
                redisTemplate.opsForZSet().rangeByScore(REDIS_KEY, fiveMinutesAgo, now);
        if (alarmJsons == null || alarmJsons.isEmpty()) return;

        for (String alarmJson : alarmJsons) {
            try {
                AlarmInfo alarmInfo = objectMapper.readValue(alarmJson, AlarmInfo.class);

                // 알람 전송
                redisAlarmPublisher.publishAlarm(alarmInfo);

                // Redis에서 현재 알람만 삭제
                redisTemplate.opsForZSet().remove(REDIS_KEY, alarmJson);

                // DB Alarm 조회
                Alarm alarm = alarmRepository.findById(alarmInfo.getAlarmId()).orElse(null);

                if (alarm != null) {
                    // 현재 울린 알람 시간이 일반 알람 시간인지, 선예매 알람 시간인지 비교 후 null 처리
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
                        if (alarm.getNormalAlarmTime() == null && alarm.getPreAlarmTime() == null) {
                            // 두 알람 다 소진됐으면 DB에서 삭제
                            alarmRepository.delete(alarm);
                        } else {
                            // 남은 알람 시간 있을 경우 업데이트 저장
                            alarmRepository.save(alarm);
                        }
                    }
                }

            } catch (Exception e) {
                log.error("Failed to process alarm: {}", alarmJson, e);
            }
        }
    }
}
