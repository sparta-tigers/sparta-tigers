package com.sparta.spartatigers.domain.watchlist.service;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import lombok.Getter;

import com.sparta.spartatigers.domain.match.model.entity.Match;
import com.sparta.spartatigers.domain.match.model.entity.Match.MatchResult;
import com.sparta.spartatigers.domain.team.model.entity.Team;
import com.sparta.spartatigers.domain.watchlist.model.entity.WatchList;

@Getter
public class StatsAccumulator {
    private static final int MIN_VISITS = 3;

    private final Team favoriteTeam;
    private int total = 0;
    private int win = 0;
    private int draw = 0;
    private int lose = 0;
    private final Map<String, Integer> stadiumVisitCount = new HashMap<>();
    private final Map<String, int[]> stadiumWdl = new HashMap<>(); // [win, total]

    public StatsAccumulator(Team favoriteTeam) {
        this.favoriteTeam = favoriteTeam;
    }

    public void accumulate(WatchList watchList) {
        Match match = watchList.getMatch();
        // 취소 경기 제외
        if (match.getMatchResult() == MatchResult.CANCEL) {
            return;
        }

        String stadiumName = match.getStadium().getName();
        stadiumVisitCount.put(stadiumName, stadiumVisitCount.getOrDefault(stadiumName, 0) + 1);

        stadiumWdl.putIfAbsent(stadiumName, new int[2]);
        int[] wdlArr = stadiumWdl.get(stadiumName);

        MatchResult myResult = determineResult(match);
        if (myResult == null) {
            return;
        }

        updateStats(myResult, wdlArr);
    }

    public double getWinRate() {
        return total > 0 ? (win * 100.0) / total : 0.0;
    }

    public String getMostVisitedStadium() {
        return stadiumVisitCount.entrySet().stream()
                .max(Entry.comparingByValue())
                .map(Entry::getKey)
                .orElse(null);
    }

    public String getBestWinRateStadium() {
        return stadiumWdl.entrySet().stream()
                .filter(e -> e.getValue()[1] >= MIN_VISITS)
                .max(Comparator.comparingDouble(e -> (double) e.getValue()[0] / e.getValue()[1]))
                .map(Entry::getKey)
                .orElse(null);
    }

    private MatchResult determineResult(Match match) {
        boolean isHome = match.getHomeTeam().equals(favoriteTeam);
        boolean isAway = match.getAwayTeam().equals(favoriteTeam);

        if (!isHome && !isAway) return null;

        MatchResult result = match.getMatchResult();
        if (isHome) return result;

        // Away일 경우 결과를 뒤집는다
        return switch (result) {
            case HOME_WIN -> MatchResult.AWAY_WIN;
            case AWAY_WIN -> MatchResult.HOME_WIN;
            default -> result;
        };
    }

    private void updateStats(MatchResult result, int[] wdlArr) {
        switch (result) {
            case HOME_WIN:
                win++;
                wdlArr[0]++;
                break;
            case AWAY_WIN:
                lose++;
                break;
            case DRAW:
                draw++;
                break;
        }
        total++;
        wdlArr[1]++; // 방문 수 증가
    }
}
