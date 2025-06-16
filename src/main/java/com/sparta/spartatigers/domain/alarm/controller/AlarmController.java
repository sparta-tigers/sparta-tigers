package com.sparta.spartatigers.domain.alarm.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
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

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/alarms")
public class AlarmController {
    private final AlarmServiceImpl alarmService;

    @GetMapping
    public ResponseEntity<List<AlarmResponseDto>> getAllAlarms(
            @AuthenticationPrincipal CustomUserPrincipal userPrincipal) {
        return ResponseEntity.ok(alarmService.findMyAlarms(userPrincipal.getUser().getId()));
    }

    @PostMapping
    public ResponseEntity<Void> createAlarm(
            @AuthenticationPrincipal CustomUserPrincipal userPrincipal,
            @Valid @RequestBody AlarmRegisterDto alarmRegisterDto) {
        alarmService.createAlarm(userPrincipal.getUser().getId(), alarmRegisterDto);
        return ResponseEntity.ok().build();
    }

    @PutMapping
    public ResponseEntity<Void> updateAlarm(
            @AuthenticationPrincipal CustomUserPrincipal userPrincipal,
            @Valid @RequestBody AlarmUpdateDto alarmUpdateDto) {
        alarmService.updateAlarm(userPrincipal.getUser().getId(), alarmUpdateDto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAlarm(
            @AuthenticationPrincipal CustomUserPrincipal customUserPrincipal,
            @Valid @RequestBody AlarmDeleteDto alarmDeleteDto) {
        alarmService.deleteAlarm(
                customUserPrincipal.getUser().getId(), alarmDeleteDto.getMatchId());
        return ResponseEntity.ok().build();
    }

    // 팀 이름 조회 (O)
    @GetMapping("/teams")
    public ResponseEntity<List<TeamNameResponseDto>> getAllTeamNames() {
        return ResponseEntity.ok(alarmService.findTeamNames());
    }

    // 월별 팀 일정 조회 (O)
    @GetMapping("/teams/{teamId}/schedules")
    public ResponseEntity<List<MatchScheduleResponseDto>> getMatchSchedule(
            @PathVariable Long teamId, @RequestParam int year, @RequestParam int month) {
        List<MatchScheduleResponseDto> schedule =
                alarmService.getMatchScheduleByTeamId(teamId, year, month);
        return ResponseEntity.ok(schedule);
    }

    // 팀 일정 세부 조회 (O)
    @GetMapping("/matches/{matchId}")
    public ResponseEntity<MatchDetailResponseDto> getMatchScheduleDetails(
            @PathVariable Long matchId) {
        return ResponseEntity.ok(alarmService.getMatchByMatchId(matchId));
    }

    @GetMapping("/sse/subscribe")
    public SseEmitter subscribe(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        Long userId = ((CustomUserPrincipal) principal).getUser().getId();
        return alarmService.subscribe(userId);
    }
}
