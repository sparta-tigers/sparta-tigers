package com.sparta.spartatigers.domain.match.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.sparta.spartatigers.domain.match.model.entity.Match;
import com.sparta.spartatigers.domain.team.model.entity.Stadium;
import com.sparta.spartatigers.domain.team.model.entity.Team;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MatchScheduleDto {

    private Long matchId;
    private LocalDateTime matchTime;
    private Team awayTeam;
    private Integer awayScore;
    private Team homeTeam;
    private Integer homeScore;
    private Stadium stadium;
    private String remark; // 비고

    public static MatchScheduleDto of(Match match) {
        return new MatchScheduleDto(
                match.getId(),
                match.getMatchTime(),
                match.getAwayTeam(),
                match.getAwayScore(),
                match.getHomeTeam(),
                match.getHomeScore(),
                match.getStadium(),
                match.getRemark());
    }
}
