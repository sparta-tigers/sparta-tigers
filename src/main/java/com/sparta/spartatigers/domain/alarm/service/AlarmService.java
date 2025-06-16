package com.sparta.spartatigers.domain.alarm.service;

import java.util.List;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.sparta.spartatigers.domain.alarm.dto.request.AlarmRegisterDto;
import com.sparta.spartatigers.domain.alarm.dto.request.AlarmUpdateDto;
import com.sparta.spartatigers.domain.alarm.dto.response.*;

public interface AlarmService {
    public AlarmRegisterDto createAlarm(Long id, AlarmRegisterDto alarmRegisterDto);

    public List<AlarmResponseDto> findMyAlarms(Long id);

    public List<TeamNameResponseDto> findTeamNames();

    public MatchScheduleResponseDto findMatchSchedules();

    public void deleteAlarm(Long userId, Long matchId);

    public AlarmUpdateDto updateAlarm(Long userId, AlarmUpdateDto alarmUpdateDto);

    public void checkAlarm();

    SseEmitter subscribe(Long id);

    List<MatchScheduleResponseDto> getMatchScheduleByTeamId(Long teamId, int year, int month);

    MatchDetailResponseDto getMatchByMatchId(Long matchId);

    public void sendAlarm(AlarmInfo alarmInfo);
}
