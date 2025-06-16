package com.sparta.spartatigers.domain.alarm.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AlarmResponseDto {
    private String homeTeam;
    private String awayTeam;
    private String stadium;
    private String matchTime;
    private LocalDateTime reservationTime;
    private LocalDateTime preReservationTime;
}
