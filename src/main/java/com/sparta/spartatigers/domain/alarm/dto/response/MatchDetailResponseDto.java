package com.sparta.spartatigers.domain.alarm.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;

import com.sparta.spartatigers.domain.match.model.entity.Match;

@Getter
@AllArgsConstructor
public class MatchDetailResponseDto {
    private Long matchId;
    private Long homeId;
    private Long awayId;
    private String homeName;
    private String awayName;
    private String stadiumName;
    private LocalDateTime matchTime;
    private LocalDateTime reservationOpenTime;

    public static MatchDetailResponseDto from(Match match) {
        return new MatchDetailResponseDto(
                match.getId(),
                match.getHomeTeam().getId(),
                match.getAwayTeam().getId(),
                match.getHomeTeam().getName(),
                match.getAwayTeam().getName(),
                match.getStadium().getName(),
                match.getMatchTime(),
                match.getReservationOpenTime());
    }
}
