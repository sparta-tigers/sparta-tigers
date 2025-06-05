package com.sparta.spartatigers.domain.watchlist.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.sparta.spartatigers.domain.watchlist.dto.RecordDto;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateWatchListRequestDto {

    private RecordDto record;
}
