package com.sparta.spartatigers.domain.alarm.service;

import org.springframework.stereotype.Service;

import com.sparta.spartatigers.domain.alarm.dto.request.AlarmRegisterDto;
import com.sparta.spartatigers.domain.alarm.dto.request.AlarmUpdateDto;
import com.sparta.spartatigers.domain.alarm.dto.response.AlarmResponseDto;
import com.sparta.spartatigers.domain.alarm.dto.response.MatchScheduleResponseDto;
import com.sparta.spartatigers.domain.alarm.dto.response.TeamNameResponseDto;
import com.sparta.spartatigers.domain.alarm.repository.AlarmRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AlarmServiceImpl implements AlarmService {
	private AlarmRepository alarmRepository;

	@Override
	public AlarmRegisterDto createAlarm() {
		return null;
	}

	@Override
	public AlarmResponseDto findMyAlarms() {
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
	public void deleteAlarms() {

	}

	@Override
	public AlarmUpdateDto updateAlarm() {
		return null;
	}
}
