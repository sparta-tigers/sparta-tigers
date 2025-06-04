package com.sparta.spartatigers.domain.watchlist.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import com.sparta.spartatigers.domain.match.dto.MatchScheduleDto;
import com.sparta.spartatigers.domain.match.model.entity.Match;
import com.sparta.spartatigers.domain.match.repository.MatchRepository;
import com.sparta.spartatigers.domain.user.model.CustomUserPrincipal;
import com.sparta.spartatigers.domain.watchlist.dto.request.CreateWatchListRequestDto;
import com.sparta.spartatigers.domain.watchlist.dto.response.CreateWatchListResponseDto;
import com.sparta.spartatigers.domain.watchlist.dto.response.WatchListResponseDto;
import com.sparta.spartatigers.domain.watchlist.model.entity.WatchList;
import com.sparta.spartatigers.domain.watchlist.repository.WatchListRepository;

@Service
@RequiredArgsConstructor
public class WatchListService {

    private final WatchListRepository watchListRepository;
    private final MatchRepository matchRepository;

    /**
     * 직관 기록 등록 서비스
     *
     * @param request 유저 요청 객체
     * @param principal 유저 정보
     * @return {@link CreateWatchListResponseDto}
     */
    public CreateWatchListResponseDto create(
            CreateWatchListRequestDto request, CustomUserPrincipal principal) {
        Match match = matchRepository.findByIdWithTeamsAndStadium(request.getMatch().getId());
        WatchList watchList = WatchList.from(match, request);

        watchListRepository.save(watchList);

        return CreateWatchListResponseDto.from(
                MatchScheduleDto.of(match), CreateWatchListResponseDto.RecordDto.of(request));
    }

    public Page<WatchListResponseDto> find(Pageable pageable) {
        Page<WatchList> all = watchListRepository.findAllByMatchId(pageable, 1L);
        if (all.isEmpty()) {
            return Page.empty();
        }
        return all.map(WatchListResponseDto::of);
    }
}
