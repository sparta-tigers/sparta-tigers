package com.sparta.spartatigers.domain.alarm.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import com.sparta.spartatigers.domain.alarm.dto.request.AlarmRegisterDto;
import com.sparta.spartatigers.domain.alarm.dto.request.AlarmUpdateDto;
import com.sparta.spartatigers.domain.alarm.dto.response.*;
import com.sparta.spartatigers.domain.alarm.model.entity.Alarm;
import com.sparta.spartatigers.domain.alarm.repository.AlarmRepository;
import com.sparta.spartatigers.domain.match.model.entity.Match;
import com.sparta.spartatigers.domain.match.repository.MatchRepository;
import com.sparta.spartatigers.domain.team.model.entity.Team;
import com.sparta.spartatigers.domain.team.repository.TeamRepository;
import com.sparta.spartatigers.domain.user.model.entity.User;
import com.sparta.spartatigers.domain.user.repository.UserRepository;
import com.sparta.spartatigers.global.exception.ExceptionCode;
import com.sparta.spartatigers.global.exception.ServerException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
@Log4j2
public class AlarmServiceImpl implements AlarmService {
    private final AlarmRepository alarmRepository;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final MatchRepository matchRepository;
    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    @Transactional
    @Override
    public AlarmRegisterDto createAlarm(Long id, AlarmRegisterDto alarmRegisterDto) {
        User user =
                userRepository
                        .findById(id)
                        .orElseThrow(() -> new ServerException(ExceptionCode.USER_NOT_FOUND));

        Match match =
                matchRepository
                        .findById(alarmRegisterDto.getId())
                        .orElseThrow(() -> new ServerException(ExceptionCode.MATCH_NOT_FOUND));

        Alarm alarm =
                Alarm.of(
                        user,
                        match,
                        alarmRegisterDto.getMinutes(),
                        alarmRegisterDto.getPreMinutes());
        alarmRepository.save(alarm);
        saveToRedis(alarm);
        return alarmRegisterDto;
    }

    @Transactional(readOnly = true)
    @Override
    public List<AlarmResponseDto> findMyAlarms(Long id) {
        User user =
                userRepository
                        .findById(id)
                        .orElseThrow(() -> new ServerException(ExceptionCode.USER_NOT_FOUND));

        List<Alarm> alarms = alarmRepository.findAllByUserIdWithMatchAndTeams(user.getId());

        return alarms.stream().map(AlarmResponseDto::from).collect(Collectors.toList());
    }

    @Override
    public List<TeamNameResponseDto> findTeamNames() {
        List<Team> teams = teamRepository.findAll();
        return teams.stream().map(TeamNameResponseDto::from).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteAlarm(Long userId, Long alarmId) {
        Alarm alarm =
                alarmRepository
                        .findById(alarmId)
                        .orElseThrow(() -> new ServerException(ExceptionCode.ALARM_NOT_FOUND));

        if (!alarm.getUser().getId().equals(userId)) {
            throw new ServerException(ExceptionCode.ACCESS_DENIED);
        }
        alarmRepository.delete(alarm);
    }

    @Override
    @Transactional
    public AlarmUpdateDto updateAlarm(Long userId, AlarmUpdateDto alarmUpdateDto) {
        Alarm alarm =
                alarmRepository
                        .findById(alarmUpdateDto.getId())
                        .orElseThrow(() -> new ServerException(ExceptionCode.ALARM_NOT_FOUND));
        if (!alarm.getUser().getId().equals(userId)) {
            throw new ServerException(ExceptionCode.ACCESS_DENIED);
        }
        LocalDateTime matchTime = alarm.getMatch().getMatchTime();
        alarm.updateAlarmTimes(
                alarmUpdateDto.getMinutes(), alarmUpdateDto.getPreMinutes(), matchTime);
        alarmRepository.save(alarm);

        return AlarmUpdateDto.from(alarm);
    }

    @Override
    public SseEmitter subscribe(Long id) {
        SseEmitter emitter = new SseEmitter(10 * 60 * 1000L);

        emitters.put(id, emitter);

        emitter.onCompletion(() -> emitters.remove(id));
        emitter.onTimeout(() -> emitters.remove(id));
        emitter.onError(e -> emitters.remove(id));

        try {
            emitter.send(
                    SseEmitter.event()
                            .name("connect")
                            .data("SSE 연결 완료")
                            .reconnectTime(3000L)); // optional
        } catch (IOException e) {
            emitter.completeWithError(e);
        }

        return emitter;
    }

    @Scheduled(fixedRate = 15_000)
    public void sendHeartbeat() {
        emitters.forEach(
                (userId, emitter) -> {
                    try {
                        emitter.send(SseEmitter.event().name("heartbeat").data(""));
                    } catch (IOException e) {
                        emitters.remove(userId);
                    }
                });
    }

    @Override
    @Transactional
    public List<MatchScheduleResponseDto> getMatchScheduleByTeamId(
            Long teamId, int year, int month) {
        teamRepository
                .findById(teamId)
                .orElseThrow(() -> new ServerException(ExceptionCode.TEAM_NOT_FOUND));

        String yearMonth = String.format("%d-%02d", year, month);
        List<Match> matches = matchRepository.findByTeamIdAndYearMonth(teamId, yearMonth);

        return matches.stream().map(MatchScheduleResponseDto::from).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public MatchDetailResponseDto getMatchByMatchId(Long matchId) {
        Match match =
                matchRepository
                        .findById(matchId)
                        .orElseThrow(() -> new ServerException(ExceptionCode.MATCH_NOT_FOUND));
        return MatchDetailResponseDto.from(match);
    }

    @Override
    public void sendAlarm(AlarmInfo alarm) {
        SseEmitter emitter = emitters.get(alarm.getUserId());
        if (emitter == null) {
            log.warn("emitter 예외 발생");
            return;
        }
        try {
            String json = objectMapper.writeValueAsString(alarm);
            String raw = "event: testAlarm\n" + "data: " + json + "\n\n";
            emitter.send(SseEmitter.event().name("testAlarm").data(raw));
        } catch (IOException e) {
            emitter.completeWithError(e);
            emitters.remove(alarm.getUserId());
            log.error("SSE 전송 실패 - userId: {}, error: {}", alarm.getUserId(), e.getMessage());
        }
    }

    private void saveToRedis(Alarm alarm) {
        try {
            ZoneOffset offset = ZoneOffset.of("+09:00");

            saveAlarmIfPresent(alarm.getNormalAlarmTime(), alarm, offset);
            saveAlarmIfPresent(alarm.getPreAlarmTime(), alarm, offset);
        } catch (JsonProcessingException e) {
            log.warn("저장 실패");
        }
    }

    private void saveAlarmIfPresent(LocalDateTime alarmTime, Alarm alarm, ZoneOffset offset)
            throws JsonProcessingException {
        if (alarmTime == null) return;

        long score = alarmTime.truncatedTo(ChronoUnit.MINUTES).toEpochSecond(offset);

        AlarmInfo info = AlarmInfo.from(alarm, alarmTime);
        String json = objectMapper.writeValueAsString(info);
        redisTemplate.opsForZSet().add("alarms", json, score);
    }
}
