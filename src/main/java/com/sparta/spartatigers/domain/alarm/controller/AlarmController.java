package com.sparta.spartatigers.domain.alarm.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import com.sparta.spartatigers.domain.alarm.service.AlarmServiceImpl;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/alarms")
public class AlarmController {
    private AlarmServiceImpl alarmService;
}
