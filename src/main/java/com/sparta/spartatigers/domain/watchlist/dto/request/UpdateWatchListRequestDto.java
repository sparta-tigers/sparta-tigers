package com.sparta.spartatigers.domain.watchlist.dto.request;

import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.sparta.spartatigers.domain.watchlist.dto.RecordDto;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateWatchListRequestDto {

    @NotNull(message = "기록 내용은 비어있을 수 없습니다.")
    private RecordDto record;
}
