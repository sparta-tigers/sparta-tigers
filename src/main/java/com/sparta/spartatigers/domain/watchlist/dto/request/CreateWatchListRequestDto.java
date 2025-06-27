package com.sparta.spartatigers.domain.watchlist.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import com.sparta.spartatigers.domain.watchlist.dto.MatchDto;
import com.sparta.spartatigers.domain.watchlist.dto.RecordDto;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CreateWatchListRequestDto {
    private MatchDto match;
    private RecordDto record;
    private String seat;
}
