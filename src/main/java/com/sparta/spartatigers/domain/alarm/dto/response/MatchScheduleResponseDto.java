package com.sparta.spartatigers.domain.alarm.dto.response;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Getter;

import com.sparta.spartatigers.domain.match.model.entity.Match;
import com.sparta.spartatigers.global.exception.ExceptionCode;
import com.sparta.spartatigers.global.exception.ServerException;

@Getter
@AllArgsConstructor
public class MatchScheduleResponseDto {
    private Long homeId;
    private Long awayId;

    @NotBlank private String homeName;
    @NotBlank private String awayName;

    @NotBlank private String stadiumName;
    private Match.MatchResult matchResult;
    private Long matchId;
    private LocalDateTime matchTime;

    private String homeTeamPath;
    private String awayTeamPath;

    private Integer homeScore;
    private Integer awayScore;

    private LocalDateTime reservationOpenTime;

    public static MatchScheduleResponseDto from(Match match) {
        if (match == null) {
            throw new ServerException(ExceptionCode.MATCH_NOT_FOUND);
        }

        return new MatchScheduleResponseDto(
                match.getHomeTeam().getId(),
                match.getAwayTeam().getId(),
                match.getHomeTeam().getName(),
                match.getAwayTeam().getName(),
                match.getStadium().getName(),
                match.getMatchResult(),
                match.getId(),
                match.getMatchTime(),
                match.getHomeTeam().getPath(),
                match.getAwayTeam().getPath(),
                match.getHomeScore(),
                match.getAwayScore(),
                match.getReservationOpenTime());
    }
}
