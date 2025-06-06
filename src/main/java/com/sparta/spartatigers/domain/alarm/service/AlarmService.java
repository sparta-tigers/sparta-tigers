package com.sparta.spartatigers.domain.alarm.service;

import java.util.List;

import com.sparta.spartatigers.domain.alarm.dto.request.AlarmRegisterDto;
import com.sparta.spartatigers.domain.alarm.dto.request.AlarmUpdateDto;
import com.sparta.spartatigers.domain.alarm.dto.response.AlarmResponseDto;
import com.sparta.spartatigers.domain.alarm.dto.response.MatchScheduleResponseDto;
import com.sparta.spartatigers.domain.alarm.dto.response.TeamNameResponseDto;

public interface AlarmService {
    public AlarmRegisterDto createAlarm();

    public List<AlarmResponseDto> findMyAlarms(Long id);

    public TeamNameResponseDto findTeamNames();

    public MatchScheduleResponseDto findMatchSchedules();

    public void deleteAlarms();

    public AlarmUpdateDto updateAlarm();
}
