package com.sparta.spartatigers.domain.match.model.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import com.sparta.spartatigers.domain.common.entity.BaseEntity;
import com.sparta.spartatigers.domain.match.dto.MatchScheduleDto;
import com.sparta.spartatigers.domain.team.model.entity.Stadium;
import com.sparta.spartatigers.domain.team.model.entity.Team;

@Entity(name = "matches")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Match extends BaseEntity {

    @Column private LocalDateTime matchTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "home_team_id", nullable = false)
    private Team homeTeam;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "away_team_id", nullable = false)
    private Team awayTeam;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stadium_id")
    private Stadium stadium;

    @Column
    @Enumerated(value = EnumType.STRING)
    private MatchResult matchResult;

    @Column private Integer homeScore;

    @Column private Integer awayScore;

    @Column private String remark; // 비고

    @Column private LocalDateTime reservationOpenTime;

    public static Match from(MatchScheduleDto matchSchedule) {
        LocalDateTime reservationOpen =
                matchSchedule
                        .getMatchTime()
                        .minusDays(7)
                        .withHour(11)
                        .withMinute(0)
                        .withSecond(0)
                        .withNano(0);

        return new Match(
                matchSchedule.getMatchTime(),
                matchSchedule.getHomeTeam(),
                matchSchedule.getAwayTeam(),
                matchSchedule.getStadium(),
                getResult(matchSchedule),
                matchSchedule.getHomeScore(),
                matchSchedule.getAwayScore(),
                matchSchedule.getRemark(),
                reservationOpen);
    }

    private static MatchResult getResult(MatchScheduleDto matchSchedule) {
        if (matchSchedule == null
                || matchSchedule.getHomeScore() == null
                || matchSchedule.getAwayScore() == null) {
            return MatchResult.CANCEL;
        }

        Integer home = matchSchedule.getHomeScore();
        Integer away = matchSchedule.getAwayScore();

        if (home > away) {
            return MatchResult.HOME_WIN;
        } else if (home < away) {
            return MatchResult.AWAY_WIN;
        } else {
            return MatchResult.DRAW;
        }
    }

    public enum MatchResult {
        HOME_WIN,
        AWAY_WIN,
        DRAW,
        CANCEL,
        NOT_PLAYED
    }
}
