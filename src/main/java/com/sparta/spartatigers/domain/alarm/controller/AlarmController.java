package com.sparta.spartatigers.domain.alarm.controller;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import com.sparta.spartatigers.domain.alarm.dto.request.AlarmRegisterDto;
import com.sparta.spartatigers.domain.alarm.dto.request.AlarmUpdateDto;
import com.sparta.spartatigers.domain.alarm.dto.response.AlarmResponseDto;
import com.sparta.spartatigers.domain.alarm.dto.response.MatchDetailResponseDto;
import com.sparta.spartatigers.domain.alarm.dto.response.MatchScheduleResponseDto;
import com.sparta.spartatigers.domain.alarm.dto.response.TeamNameResponseDto;
import com.sparta.spartatigers.domain.alarm.service.AlarmService;
import com.sparta.spartatigers.domain.user.model.CustomUserPrincipal;
import com.sparta.spartatigers.global.response.ApiResponse;
import com.sparta.spartatigers.global.response.MessageCode;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/alarms")
public class AlarmController {
    private final AlarmService alarmService;

    // 알람 조회 (O)
    @GetMapping
    public ApiResponse<List<AlarmResponseDto>> getAllAlarms(
            @AuthenticationPrincipal CustomUserPrincipal userPrincipal) {
        List<AlarmResponseDto> response =
                alarmService.findMyAlarms(userPrincipal.getUser().getId());
        return ApiResponse.ok(response);
    }

    // 알람 생성 (O)
    @PostMapping
    public ApiResponse<String> createAlarm(
            @AuthenticationPrincipal CustomUserPrincipal userPrincipal,
            @Valid @RequestBody AlarmRegisterDto alarmRegisterDto) {

        alarmService.createAlarm(userPrincipal.getUser().getId(), alarmRegisterDto);

        return ApiResponse.created(MessageCode.ALARM_REQUEST_SUCCESS.getMessage());
    }

    // 알람 수정 (O)
    @PutMapping
    public ApiResponse<String> updateAlarm(
            @AuthenticationPrincipal CustomUserPrincipal userPrincipal,
            @Valid @RequestBody AlarmUpdateDto alarmUpdateDto) {
        alarmService.updateAlarm(userPrincipal.getUser().getId(), alarmUpdateDto);
        return ApiResponse.ok(MessageCode.ALARM_REQUEST_SUCCESS.getMessage());
    }

    // 알람 삭제 (O)
    @DeleteMapping("/{alarmId}")
    public ApiResponse<String> deleteAlarm(
            @AuthenticationPrincipal CustomUserPrincipal customUserPrincipal,
            @PathVariable Long alarmId) {
        alarmService.deleteAlarm(customUserPrincipal.getUser().getId(), alarmId);
        return ApiResponse.ok(MessageCode.ALARM_REQUEST_SUCCESS.getMessage());
    }

    // 팀 이름 조회 (O)
    @GetMapping("/teams")
    public ApiResponse<List<TeamNameResponseDto>> getAllTeamNames() {
        return ApiResponse.ok(alarmService.findTeamNames());
    }

    // 월별 팀 일정 조회 매치 타임 기준 (O)
    @GetMapping("/teams/{teamId}/schedules")
    public ApiResponse<List<MatchScheduleResponseDto>> getMatchSchedule(
            @PathVariable Long teamId,
            @RequestParam @Min(2020) @Max(2100) int year,
            @RequestParam @Min(1) @Max(12) int month) {
        List<MatchScheduleResponseDto> responseDtos =
                alarmService.getMatchScheduleByTeamId(teamId, year, month);
        return ApiResponse.ok(responseDtos);
    }

    @GetMapping("/teams/{teamId}/reservation-open-schedules")
    public ApiResponse<List<MatchScheduleResponseDto>> getReservationOpenSchedule(
            @PathVariable Long teamId,
            @RequestParam @Min(2020) @Max(2100) int year,
            @RequestParam @Min(1) @Max(12) int month) {
        List<MatchScheduleResponseDto> responseDtos =
                alarmService.getReservationOpenScheduleByTeamId(teamId, year, month);
        return ApiResponse.ok(responseDtos);
    }

    // 팀 일정 세부 조회 (O)
    @GetMapping("/matches/{matchId}")
    public ApiResponse<MatchDetailResponseDto> getMatchScheduleDetails(@PathVariable Long matchId) {
        MatchDetailResponseDto responseDtos = alarmService.getMatchByMatchId(matchId);
        return ApiResponse.ok(responseDtos);
    }

    // 알람 (O)
    @GetMapping(path = "/sse/subscribe/{userId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@PathVariable Long userId) {
        return alarmService.subscribe(userId);
    }
}
