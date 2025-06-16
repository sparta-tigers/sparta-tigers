package com.sparta.spartatigers.domain.alarm.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import com.sparta.spartatigers.domain.alarm.dto.request.AlarmDeleteDto;
import com.sparta.spartatigers.domain.alarm.dto.request.AlarmRegisterDto;
import com.sparta.spartatigers.domain.alarm.dto.request.AlarmUpdateDto;
import com.sparta.spartatigers.domain.alarm.dto.response.AlarmResponseDto;
import com.sparta.spartatigers.domain.alarm.dto.response.MatchDetailResponseDto;
import com.sparta.spartatigers.domain.alarm.dto.response.MatchScheduleResponseDto;
import com.sparta.spartatigers.domain.alarm.dto.response.TeamNameResponseDto;
import com.sparta.spartatigers.domain.alarm.service.AlarmServiceImpl;
import com.sparta.spartatigers.domain.user.model.CustomUserPrincipal;
import com.sparta.spartatigers.global.response.ApiResponse;
import com.sparta.spartatigers.global.response.MessageCode;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/alarms")
public class AlarmController {
    private final AlarmServiceImpl alarmService;

    @GetMapping
    public ApiResponse<List<AlarmResponseDto>> getAllAlarms(
            @AuthenticationPrincipal CustomUserPrincipal userPrincipal) {
        List<AlarmResponseDto> response =
                alarmService.findMyAlarms(userPrincipal.getUser().getId());
        return ApiResponse.ok(response);
    }

    @PostMapping
    public ApiResponse<String> createAlarm(
            @AuthenticationPrincipal CustomUserPrincipal userPrincipal,
            @Valid @RequestBody AlarmRegisterDto alarmRegisterDto) {

        alarmService.createAlarm(userPrincipal.getUser().getId(), alarmRegisterDto);

        return ApiResponse.created(MessageCode.ALARM_REQUEST_SUCCESS.getMessage());
    }

    @PutMapping
    public ApiResponse<String> updateAlarm(
            @AuthenticationPrincipal CustomUserPrincipal userPrincipal,
            @Valid @RequestBody AlarmUpdateDto alarmUpdateDto) {
        alarmService.updateAlarm(userPrincipal.getUser().getId(), alarmUpdateDto);
        return ApiResponse.ok(MessageCode.ALARM_REQUEST_SUCCESS.getMessage());
    }

    @DeleteMapping
    public ApiResponse<String> deleteAlarm(
            @AuthenticationPrincipal CustomUserPrincipal customUserPrincipal,
            @Valid @RequestBody AlarmDeleteDto alarmDeleteDto) {
        alarmService.deleteAlarm(
                customUserPrincipal.getUser().getId(), alarmDeleteDto.getMatchId());
        return ApiResponse.ok(MessageCode.ALARM_REQUEST_SUCCESS.getMessage());
    }

    // 팀 이름 조회 (O)
    @GetMapping("/teams")
    public ApiResponse<List<TeamNameResponseDto>> getAllTeamNames() {
        return ApiResponse.ok(alarmService.findTeamNames());
    }

    // 월별 팀 일정 조회 (O)
    @GetMapping("/teams/{teamId}/schedules")
    public ApiResponse<List<MatchScheduleResponseDto>> getMatchSchedule(
            @PathVariable Long teamId, @RequestParam int year, @RequestParam int month) {
        List<MatchScheduleResponseDto> responseDtos =
                alarmService.getMatchScheduleByTeamId(teamId, year, month);
        return ApiResponse.ok(responseDtos);
    }

    // 팀 일정 세부 조회 (O)
    @GetMapping("/matches/{matchId}")
    public ApiResponse<MatchDetailResponseDto> getMatchScheduleDetails(@PathVariable Long matchId) {
        MatchDetailResponseDto responseDtos = alarmService.getMatchByMatchId(matchId);
        return ApiResponse.ok(responseDtos);
    }

    @GetMapping("/sse/subscribe")
    public SseEmitter subscribe(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        Long userId = ((CustomUserPrincipal) principal).getUser().getId();
        return alarmService.subscribe(userId);
    }
}
