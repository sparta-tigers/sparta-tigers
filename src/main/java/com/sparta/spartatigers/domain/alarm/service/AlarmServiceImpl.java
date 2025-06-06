package com.sparta.spartatigers.domain.alarm.service;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import com.sparta.spartatigers.domain.alarm.dto.request.AlarmRegisterDto;
import com.sparta.spartatigers.domain.alarm.dto.request.AlarmUpdateDto;
import com.sparta.spartatigers.domain.alarm.dto.response.AlarmResponseDto;
import com.sparta.spartatigers.domain.alarm.dto.response.MatchScheduleResponseDto;
import com.sparta.spartatigers.domain.alarm.dto.response.TeamNameResponseDto;
import com.sparta.spartatigers.domain.alarm.repository.AlarmRepository;

@Service
@RequiredArgsConstructor
public class AlarmServiceImpl implements AlarmService {
    private AlarmRepository alarmRepository;

    @Override
    public AlarmRegisterDto createAlarm() {
        return null;
    }

    @Override
    public List<AlarmResponseDto> findMyAlarms(Long id) {
        return null;
    }

    @Override
    public TeamNameResponseDto findTeamNames() {
        return null;
    }

    @Override
    public MatchScheduleResponseDto findMatchSchedules() {
        return null;
    }

    @Override
    public void deleteAlarms() {}

    @Override
    public AlarmUpdateDto updateAlarm() {
        return null;
    }
}
