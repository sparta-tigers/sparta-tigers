package com.sparta.spartatigers.domain.alarm.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import com.sparta.spartatigers.domain.alarm.dto.request.AlarmRegisterDto;
import com.sparta.spartatigers.domain.alarm.dto.request.AlarmUpdateDto;
import com.sparta.spartatigers.domain.alarm.dto.response.AlarmResponseDto;
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

    @GetMapping("/teams")
    public ResponseEntity<List<TeamNameResponseDto>> getAllTeamNames() {
        return ResponseEntity.ok(alarmService.findTeamNames());
    }

    @GetMapping("/matches/{id}")
    public ResponseEntity<MatchScheduleResponseDto> getMatchSchedule(@PathVariable Long id) {
        //		MatchScheduleResponseDto schedule = matchService.getMatchScheduleByTeamId(id);
        //		return ResponseEntity.ok(schedule);
        return null;
    }

    @GetMapping("/sse/subscribe")
    public SseEmitter subscribe(Authentication authentication) {
        Object principal = authentication.getPrincipal();

        Long userId = null;
        if (principal instanceof CustomUserPrincipal) {
            userId = ((CustomUserPrincipal) principal).getUser().getId();
            log.info(userId + "유저 아디1");
        } else if (principal instanceof DefaultOAuth2User) {
            DefaultOAuth2User oauthUser = (DefaultOAuth2User) principal;

            // OAuth2User에서 ID 추출 (OAuth 제공자에 따라 다름)
            // 예: 카카오 로그인이라면 "id"라는 키에 사용자 ID가 있을 수 있음
            Object idAttr = oauthUser.getAttribute("id");
            if (idAttr instanceof Number) {
                userId = ((Number) idAttr).longValue();
            } else if (idAttr instanceof String) {
                userId = Long.valueOf((String) idAttr);
            } else {
                throw new IllegalArgumentException("OAuth2User에서 사용자 ID를 찾을 수 없습니다.");
            }
        } else {
            throw new IllegalStateException("인증된 사용자 타입을 알 수 없습니다.");
        }

        return alarmService.subscribe(userId);
    }

    @PostMapping("/send")
    public ResponseEntity<String> sendAlarm(Authentication authentication) {
        try {
            Object principal = authentication.getPrincipal();
            Long userId = null;

            if (principal instanceof CustomUserPrincipal) {
                userId = ((CustomUserPrincipal) principal).getUser().getId();
                log.info(userId + "유저 아디2");
            } else if (principal instanceof DefaultOAuth2User) {
                DefaultOAuth2User oauthUser = (DefaultOAuth2User) principal;
                Object idAttr = oauthUser.getAttribute("id");
                if (idAttr instanceof Number) {
                    userId = ((Number) idAttr).longValue();
                } else if (idAttr instanceof String) {
                    userId = Long.valueOf((String) idAttr);
                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("OAuth2User에서 사용자 ID를 찾을 수 없습니다.");
                }
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증된 사용자 타입을 알 수 없습니다.");
            }

            alarmService.sendAlarm(userId);
            return ResponseEntity.ok("알람 전송 완료");

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("알람 전송 실패");
        }
    }
}
