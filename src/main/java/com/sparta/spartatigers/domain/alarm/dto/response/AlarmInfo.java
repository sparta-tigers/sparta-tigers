package com.sparta.spartatigers.domain.alarm.dto.response;

import java.time.LocalDateTime;

import lombok.*;

import com.sparta.spartatigers.domain.alarm.model.entity.Alarm;

import com.fasterxml.jackson.annotation.JsonFormat;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AlarmInfo {
    private Long alarmId;
    private Long userId;
    private Long matchId;
    private String homeTeam;
    private String awayTeam;
    private String stadium;
    private LocalDateTime matchTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime alarmTime;

    public static AlarmInfo from(Alarm alarm, LocalDateTime alarmTime) {
        return new AlarmInfo(
                alarm.getId(),
                alarm.getUser().getId(),
                alarm.getMatch().getId(),
                alarm.getMatch().getHomeTeam().getName(),
                alarm.getMatch().getAwayTeam().getName(),
                alarm.getMatch().getStadium().getName(),
                alarm.getMatch().getMatchTime(),
                alarmTime);
    }
}
