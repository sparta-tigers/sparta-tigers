package com.sparta.spartatigers.domain.watchlist.dto.request;

import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.sparta.spartatigers.domain.watchlist.dto.MatchDto;
import com.sparta.spartatigers.domain.watchlist.dto.RecordDto;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateWatchListRequestDto {

    @NotNull(message = "경기 정보는 비어있을 수 없습니다.")
    private MatchDto match;

    @NotNull(message = "기록 내용은 비어있을 수 없습니다.")
    private RecordDto record;

    private String seat;
}
