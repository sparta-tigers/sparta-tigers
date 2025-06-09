package com.sparta.spartatigers.domain.watchlist.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.sparta.spartatigers.domain.watchlist.service.StatsAccumulator;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StatsResponseDto {

    private int total;
    private double winRate;
    private String mostVisitedStadium;
    private String bestWinRateStadium;
    private int win, draw, lose;

    public static StatsResponseDto of(StatsAccumulator accumulator) {
        return new StatsResponseDto(
                accumulator.getTotal(),
                accumulator.getWinRate(),
                accumulator.getMostVisitedStadium(),
                accumulator.getBestWinRateStadium(),
                accumulator.getWin(),
                accumulator.getDraw(),
                accumulator.getLose());
    }
}
