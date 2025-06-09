package com.sparta.spartatigers.domain.watchlist.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StatsResponseDto {

    private int total;
    private double winRate;
    private String mostVisitStadium;
    private String bestWinRateStadium;
    private int win, draw, lose;
}
