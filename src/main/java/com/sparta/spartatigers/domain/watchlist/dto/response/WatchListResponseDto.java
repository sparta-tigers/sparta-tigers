package com.sparta.spartatigers.domain.watchlist.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.sparta.spartatigers.domain.match.dto.MatchScheduleDto;
import com.sparta.spartatigers.domain.watchlist.dto.response.CreateWatchListResponseDto.RecordDto;
import com.sparta.spartatigers.domain.watchlist.model.entity.WatchList;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class WatchListResponseDto {
    private RecordDto recordDto;
    private MatchScheduleDto matchScheduleDto;

    public static WatchListResponseDto of(WatchList watchList) {
        MatchScheduleDto matchScheduleDto = MatchScheduleDto.of(watchList.getMatch());
        RecordDto recordDto = RecordDto.of(watchList);
        return new WatchListResponseDto(recordDto, matchScheduleDto);
    }
}
