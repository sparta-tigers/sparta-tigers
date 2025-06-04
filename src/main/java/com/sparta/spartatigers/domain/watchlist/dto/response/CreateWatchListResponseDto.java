package com.sparta.spartatigers.domain.watchlist.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.sparta.spartatigers.domain.match.dto.MatchScheduleDto;
import com.sparta.spartatigers.domain.watchlist.dto.request.CreateWatchListRequestDto;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateWatchListResponseDto {

    private MatchScheduleDto match;
    private RecordDto record;

    public static CreateWatchListResponseDto of() {
        return new CreateWatchListResponseDto();
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
    }
}
