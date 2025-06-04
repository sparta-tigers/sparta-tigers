package com.sparta.spartatigers.domain.watchlist.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import com.sparta.spartatigers.domain.match.dto.MatchScheduleDto;
import com.sparta.spartatigers.domain.match.model.entity.Match;
import com.sparta.spartatigers.domain.match.repository.MatchRepository;
import com.sparta.spartatigers.domain.watchlist.dto.request.CreateWatchListRequestDto;
import com.sparta.spartatigers.domain.watchlist.dto.response.CreateWatchListResponseDto;
import com.sparta.spartatigers.domain.watchlist.model.entity.WatchList;
import com.sparta.spartatigers.domain.watchlist.repository.WatchListRepository;

@Service
@RequiredArgsConstructor
public class WatchListService {

    private final WatchListRepository watchListRepository;
    private final MatchRepository matchRepository;

    public CreateWatchListResponseDto create(CreateWatchListRequestDto request) {
        Match match = matchRepository.findByIdWithTeamsAndStadium(request.getMatch().getId());

        WatchList watchList = WatchList.from(match, request);
        watchListRepository.save(watchList);

        MatchScheduleDto findMatch = MatchScheduleDto.of(match);

        return new CreateWatchListResponseDto(
                findMatch, CreateWatchListResponseDto.RecordDto.of(request));
    }
}
