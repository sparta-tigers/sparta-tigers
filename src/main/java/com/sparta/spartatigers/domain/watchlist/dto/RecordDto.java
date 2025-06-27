package com.sparta.spartatigers.domain.watchlist.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.sparta.spartatigers.domain.watchlist.dto.request.CreateWatchListRequestDto;
import com.sparta.spartatigers.domain.watchlist.model.entity.WatchList;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RecordDto {

    @NotBlank(message = "내용은 비어있을 수 없습니다.")
    private String content;

    @NotNull(message = "평점은 비어있을 수 없습니다.")
    @Min(value = 1, message = "평점은 최소 1점 이상입니다.")
    private Integer rate;

    public static RecordDto of(CreateWatchListRequestDto dto) {
        return new RecordDto(dto.getRecord().getContent(), dto.getRecord().getRate());
    }

    public static RecordDto of(WatchList watchList) {
        return new RecordDto(watchList.getContents(), watchList.getRating());
    }
}
