package com.sparta.spartatigers.domain.watchlist.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.sparta.spartatigers.domain.match.dto.MatchScheduleDto;
import com.sparta.spartatigers.domain.watchlist.dto.RecordDto;
import com.sparta.spartatigers.domain.watchlist.model.entity.WatchList;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateWatchListResponseDto {

    private MatchScheduleDto match;
    private RecordDto record;
    private String seat;

    public static CreateWatchListResponseDto of(WatchList watchList) {
        return new CreateWatchListResponseDto(
                MatchScheduleDto.of(watchList.getMatch()),
                RecordDto.of(watchList),
                watchList.getSeat());
    }
}
