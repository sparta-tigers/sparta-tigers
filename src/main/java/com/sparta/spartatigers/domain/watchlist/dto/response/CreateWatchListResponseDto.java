package com.sparta.spartatigers.domain.watchlist.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.sparta.spartatigers.domain.match.dto.MatchScheduleDto;
import com.sparta.spartatigers.domain.match.model.entity.Match;
import com.sparta.spartatigers.domain.watchlist.dto.RecordDto;
import com.sparta.spartatigers.domain.watchlist.dto.request.CreateWatchListRequestDto;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateWatchListResponseDto {

    private MatchScheduleDto match;
    private RecordDto record;

    public static CreateWatchListResponseDto from(Match match, RecordDto recordDto) {
        return new CreateWatchListResponseDto(MatchScheduleDto.of(match), recordDto);
    }
}
