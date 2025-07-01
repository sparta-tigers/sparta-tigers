package com.sparta.spartatigers.domain.alarm.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.sparta.spartatigers.domain.alarm.dto.request.AlarmRegisterDto;
import com.sparta.spartatigers.domain.alarm.dto.request.AlarmUpdateDto;
import com.sparta.spartatigers.domain.alarm.dto.response.AlarmResponseDto;
import com.sparta.spartatigers.domain.alarm.dto.response.MatchDetailResponseDto;
import com.sparta.spartatigers.domain.alarm.dto.response.MatchScheduleResponseDto;
import com.sparta.spartatigers.domain.alarm.dto.response.TeamNameResponseDto;
import com.sparta.spartatigers.domain.alarm.service.AlarmService;
import com.sparta.spartatigers.domain.user.model.CustomUserPrincipal;
import com.sparta.spartatigers.domain.user.service.UserService;
import com.sparta.spartatigers.global.exception.ExceptionCode;
import com.sparta.spartatigers.global.exception.ServerException;
import com.sparta.spartatigers.global.response.ApiResponse;
import com.sparta.spartatigers.global.response.MessageCode;
import com.sparta.spartatigers.global.util.JwtUtil;

import io.jsonwebtoken.Claims;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/alarms")
public class AlarmController {
	private final AlarmService alarmService;
	private final JwtUtil jwtUtil;
	private final UserService userService;

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

	// 월별 팀 일정 조회 (O)
	@GetMapping("/teams/{teamId}/schedules")
	public ApiResponse<List<MatchScheduleResponseDto>> getMatchSchedule(
		@PathVariable Long teamId,
		@RequestParam @Min(2020) @Max(2100) int year,
		@RequestParam @Min(1) @Max(12) int month) {
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
	public SseEmitter subscribe(@RequestParam String token) {

		Claims claims = jwtUtil.validateToken(token);
		System.out.println(claims);
		if (claims == null) {
			throw new ServerException(ExceptionCode.NOT_FOUND_JWT);
		}

		String email = claims.getSubject();

		Long userId = userService.findUserIdByEmail(email);

		return alarmService.subscribe(userId);
	}

	// @GetMapping("/sse/subscribe")
	// public SseEmitter subscribe(Authentication authentication) {
	// 	CustomUserPrincipal principal = (CustomUserPrincipal)authentication.getPrincipal();
	// 	Long userId = principal.getUser().getId();
	// 	return alarmService.subscribe(userId);
	// }
}
