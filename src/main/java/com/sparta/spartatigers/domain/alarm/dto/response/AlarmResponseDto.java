package com.sparta.spartatigers.domain.alarm.dto.response;

import java.time.LocalDateTime;
import java.time.LocalTime;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AlarmResponseDto {
    @NotBlank private String homeTeam;
    @NotBlank private String awayTeam;
    @NotBlank private String stadium;
    private LocalDateTime reservationTime;
    private LocalDateTime preReservationTime;
    private LocalTime alarmTime;
    private LocalTime preAlarmTime;

    @AssertTrue(message = "reservationTime 또는 preReservationTime 중 하나는 필수입니다.")
    public boolean isAtLeastOneSetReservation() {
        return reservationTime != null || preReservationTime != null;
    }

    @AssertTrue(message = "alarm 또는 preAlarm 중 하나는 필수입니다.")
    public boolean isAtLeastOneSetAlarm() {
        return alarmTime != null || preAlarmTime != null;
    }
}
