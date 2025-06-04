package com.sparta.spartatigers.domain.watchlist.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.sparta.spartatigers.domain.watchlist.dto.MatchDto;
import com.sparta.spartatigers.domain.watchlist.dto.RecordDto;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateWatchListRequestDto {

    private MatchDto match;
    private RecordDto record;
}
