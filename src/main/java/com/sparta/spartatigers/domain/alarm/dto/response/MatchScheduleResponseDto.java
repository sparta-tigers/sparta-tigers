package com.sparta.spartatigers.domain.alarm.dto.response;

import java.time.LocalDateTime;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;

import lombok.Getter;

@Getter
public class MatchScheduleResponseDto {
    @NotBlank private String homeName;
    @NotBlank private String awayName;
    @NotBlank private String stadiumName;

    private LocalDateTime reservationTime;
    private LocalDateTime preReservationTime;

    @AssertTrue(message = "reservationTime 또는 preReservationTime 중 하나는 필수입니다.")
    public boolean isAtLeastOneSet() {
        return reservationTime != null || preReservationTime != null;
    }
}
