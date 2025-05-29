package com.sparta.spartatigers.domain.alarm.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sparta.spartatigers.domain.alarm.dto.response.AlarmResponseDto;
import com.sparta.spartatigers.domain.alarm.service.AlarmServiceImpl;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/alarms")
public class AlarmController {
	private AlarmServiceImpl alarmService;
}
