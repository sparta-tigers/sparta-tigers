package com.sparta.spartatigers.domain.watchlist.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateWatchListRequestDto {

    private MatchDto match;
    private RecordDto record;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MatchDto {

        private Long id;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecordDto {

        private String content;
        private int rate;
    }
}
