package com.sparta.spartatigers.domain.watchlist.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.sparta.spartatigers.domain.match.dto.MatchScheduleDto;
import com.sparta.spartatigers.domain.watchlist.dto.RecordDto;
import com.sparta.spartatigers.domain.watchlist.model.entity.WatchList;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class WatchListResponseDto {
    private Long id;
    private RecordDto record;
    private MatchScheduleDto match;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static WatchListResponseDto of(WatchList watchList) {
        MatchScheduleDto matchScheduleDto = MatchScheduleDto.of(watchList.getMatch());
        return new WatchListResponseDto(
                watchList.getId(),
                RecordDto.of(watchList),
                matchScheduleDto,
                watchList.getCreatedAt(),
                watchList.getUpdatedAt());
    }
}
