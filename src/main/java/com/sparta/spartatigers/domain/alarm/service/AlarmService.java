package com.sparta.spartatigers.domain.alarm.service;

import com.sparta.spartatigers.domain.alarm.dto.request.AlarmRegisterDto;
import com.sparta.spartatigers.domain.alarm.dto.request.AlarmUpdateDto;
import com.sparta.spartatigers.domain.alarm.dto.response.AlarmResponseDto;
import com.sparta.spartatigers.domain.alarm.dto.response.MatchScheduleResponseDto;
import com.sparta.spartatigers.domain.alarm.dto.response.TeamNameResponseDto;

public interface AlarmService {
    public AlarmRegisterDto createAlarm();

    public AlarmResponseDto findMyAlarms();

    public TeamNameResponseDto findTeamNames();

    public MatchScheduleResponseDto findMatchSchedules();

    public void deleteAlarms();

    public AlarmUpdateDto updateAlarm();
}
