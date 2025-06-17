package com.sparta.spartatigers.domain.alarm.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;

import com.sparta.spartatigers.domain.alarm.model.entity.Alarm;
import com.sparta.spartatigers.domain.match.model.entity.Match;

@Getter
@AllArgsConstructor
public class AlarmResponseDto {
    private String homeTeam;
    private String awayTeam;
    private String stadium;
    private LocalDateTime matchTime;
    private LocalDateTime reservationTime;
    private LocalDateTime preReservationTime;

    public static AlarmResponseDto from(Alarm alarm) {
        Match match = alarm.getMatch();
        return new AlarmResponseDto(
                match.getHomeTeam().getName(),
                match.getAwayTeam().getName(),
                match.getStadium().getName(),
                match.getMatchTime(),
                alarm.getNormalAlarmTime(),
                alarm.getPreAlarmTime());
    }
}
