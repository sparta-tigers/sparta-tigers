package com.sparta.spartatigers.domain.watchlist.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SearchWatchListRequestDto {

    private String teamName;
    private String stadiumName;
}
