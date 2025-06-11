package com.sparta.spartatigers.domain.alarm.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import lombok.RequiredArgsConstructor;

import com.sparta.spartatigers.domain.alarm.dto.request.AlarmRegisterDto;
import com.sparta.spartatigers.domain.alarm.dto.request.AlarmUpdateDto;
import com.sparta.spartatigers.domain.alarm.dto.response.AlarmResponseDto;
import com.sparta.spartatigers.domain.alarm.dto.response.MatchScheduleResponseDto;
import com.sparta.spartatigers.domain.alarm.dto.response.TeamNameResponseDto;
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

@Service
@RequiredArgsConstructor
public class AlarmServiceImpl implements AlarmService {
    private final AlarmRepository alarmRepository;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final MatchRepository matchRepository;
    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

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
        return alarmRegisterDto;
    }

    @Transactional(readOnly = true)
    @Override
    public List<AlarmResponseDto> findMyAlarms(Long id) {
        User user =
                userRepository
                        .findById(id)
                        .orElseThrow(() -> new ServerException(ExceptionCode.USER_NOT_FOUND));

        Optional<Alarm> alarms = alarmRepository.findById(id);

        return alarms.stream()
                .map(
                        alarm -> {
                            Match match = alarm.getMatch();

                            return new AlarmResponseDto(
                                    match.getHomeTeam().getName(),
                                    match.getAwayTeam().getName(),
                                    match.getStadium().getName(),
                                    alarm.getNormalAlarmTime(),
                                    alarm.getPreAlarmTime(),
                                    alarm.getNormalAlarmTime() != null
                                            ? alarm.getNormalAlarmTime().toLocalTime()
                                            : null,
                                    alarm.getPreAlarmTime() != null
                                            ? alarm.getPreAlarmTime().toLocalTime()
                                            : null);
                        })
                .collect(Collectors.toList());
    }

    @Override
    public List<TeamNameResponseDto> findTeamNames() {
        List<Team> teams = teamRepository.findAll();
        return teams.stream()
                .map(team -> new TeamNameResponseDto(team.getId(), team.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public MatchScheduleResponseDto findMatchSchedules() {
        return null;
    }

    @Override
    public void deleteAlarms() {}

    @Override
    public AlarmUpdateDto updateAlarm(Long id, AlarmUpdateDto alarmUpdateDto) {
        return null;
    }

    @Override
    public void checkAlarm() {}

    @Override
    public SseEmitter subscribe(Long id) {
        SseEmitter emitter = new SseEmitter(60 * 60 * 1000L); // 1시간 유지
        emitters.put(id, emitter);

        emitter.onCompletion(() -> emitters.remove(id));
        emitter.onTimeout(() -> emitters.remove(id));

        try {
            emitter.send(SseEmitter.event().name("connect").data("SSE 연결 완료"));
        } catch (IOException e) {
            emitter.completeWithError(e);
        }

        return emitter;
    }

    public void sendAlarm(Long userId) {
        String message = "📢 설정한 시간에 알람이 도착했습니다!";
        SseEmitter emitter = emitters.get(userId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event().name("alarm").data(message));
            } catch (IOException e) {
                emitters.remove(userId);
            }
        }
    }

    @Scheduled(cron = "0 * * * * *") // 매 분마다
    public void sendAlarmsToAllUsers() {
        System.out.println("TEST dkffka");
        for (Long userId : emitters.keySet()) {
            sendAlarm(userId);
        }
    }
}
