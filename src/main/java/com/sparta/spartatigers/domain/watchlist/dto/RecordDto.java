package com.sparta.spartatigers.domain.watchlist.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.sparta.spartatigers.domain.watchlist.dto.request.CreateWatchListRequestDto;
import com.sparta.spartatigers.domain.watchlist.model.entity.WatchList;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RecordDto {

    private String content;
    private int rate;

    public static RecordDto of(CreateWatchListRequestDto dto) {
        return new RecordDto(dto.getRecord().getContent(), dto.getRecord().getRate());
    }

    public static RecordDto of(WatchList watchList) {
        return new RecordDto(watchList.getContents(), watchList.getRating());
    }
}
