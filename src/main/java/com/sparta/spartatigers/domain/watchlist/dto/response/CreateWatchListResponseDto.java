package com.sparta.spartatigers.domain.watchlist.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.sparta.spartatigers.domain.match.dto.MatchScheduleDto;
import com.sparta.spartatigers.domain.watchlist.dto.request.CreateWatchListRequestDto;
import com.sparta.spartatigers.domain.watchlist.model.entity.WatchList;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateWatchListResponseDto {

    private MatchScheduleDto match;
    private RecordDto record;

    public static CreateWatchListResponseDto from(MatchScheduleDto match, RecordDto record) {
        return new CreateWatchListResponseDto(match, record);
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecordDto {

        private String content;
        private int rate;

        public static RecordDto of(CreateWatchListRequestDto dto) {
            return new RecordDto(dto.getRecord().getContent(), dto.getRecord().getRate());
        }

        public static RecordDto of(WatchList watchList) {
            return new RecordDto(watchList.getContents(), watchList.getRating());
        }
    }
}
