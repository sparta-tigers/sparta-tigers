package com.sparta.spartatigers.domain.watchlist.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.sparta.spartatigers.domain.match.model.entity.Match;
import com.sparta.spartatigers.domain.match.repository.MatchRepository;
import com.sparta.spartatigers.domain.user.model.CustomUserPrincipal;
import com.sparta.spartatigers.domain.watchlist.dto.RecordDto;
import com.sparta.spartatigers.domain.watchlist.dto.request.CreateWatchListRequestDto;
import com.sparta.spartatigers.domain.watchlist.dto.request.SearchWatchListRequestDto;
import com.sparta.spartatigers.domain.watchlist.dto.request.UpdateWatchListRequestDto;
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
    @Transactional
    public CreateWatchListResponseDto create(
            CreateWatchListRequestDto request, CustomUserPrincipal principal) {
        Match match = matchRepository.findByIdWithTeamsAndStadium(request.getMatch().getId());
        WatchList watchList = WatchList.from(match, request, principal.getUser());

        watchListRepository.save(watchList);

        return CreateWatchListResponseDto.from(match, RecordDto.of(request));
    }

    /**
     * 직관 기록 다건 조회 서비스
     *
     * @param pageable 페이지 정보
     * @param principal 유저 정보
     * @return {@link WatchListResponseDto} 페이지
     */
    @Transactional(readOnly = true)
    public Page<WatchListResponseDto> findAll(Pageable pageable, CustomUserPrincipal principal) {
        Long userId = CustomUserPrincipal.getUserId(principal);
        Page<WatchList> all = watchListRepository.findAllByUserIdWithMatchDetails(userId, pageable);

        return all.map(WatchListResponseDto::of);
    }

    /**
     * 직관 기록 단건 조회 서비스
     *
     * @param watchListId 직관 기록 식별자
     * @param principal 유저 정보
     * @return {@link WatchListResponseDto}
     */
    @Transactional(readOnly = true)
    public WatchListResponseDto findOne(Long watchListId, CustomUserPrincipal principal) {
        Long userId = CustomUserPrincipal.getUserId(principal);
        WatchList findWatchList =
                watchListRepository.findDetailByIdAndOwnerOrThrow(watchListId, userId);

        return WatchListResponseDto.of(findWatchList);
    }

    /**
     * 직관 기록 수정 서비스
     *
     * @param watchListId 직관 기록 식별자
     * @param request 요청 DTO
     * @param principal 유저 정보
     * @return {@link WatchListResponseDto}
     */
    @Transactional
    public WatchListResponseDto update(
            Long watchListId, UpdateWatchListRequestDto request, CustomUserPrincipal principal) {
        Long userId = CustomUserPrincipal.getUserId(principal);
        WatchList findWatchList =
                watchListRepository.findDetailByIdAndOwnerOrThrow(watchListId, userId);

        findWatchList.update(request.getRecord().getContent(), request.getRecord().getRate());

        return WatchListResponseDto.of(findWatchList);
    }

    /**
     * 직관 기록 삭제
     *
     * @param watchListId 직관 기록 식별자
     * @param principal 유저 정보
     */
    @Transactional
    public void delete(Long watchListId, CustomUserPrincipal principal) {
        Long userId = CustomUserPrincipal.getUserId(principal);
        WatchList findWatchList =
                watchListRepository.findDetailByIdAndOwnerOrThrow(watchListId, userId);

        watchListRepository.delete(findWatchList);
    }

    /**
     * 직관 기록 검색 서비스
     *
     * @param request 검색 요청 객체
     * @param principal 유저 정보
     * @return {@link Page<WatchListResponseDto>}
     */
    @Transactional(readOnly = true)
    public Page<WatchListResponseDto> search(
            Pageable pageable, SearchWatchListRequestDto request, CustomUserPrincipal principal) {
        Long userId = CustomUserPrincipal.getUserId(principal);
        Page<WatchList> all =
                watchListRepository.findAllByKeyword(
                        userId, request.getTeamName(), request.getStadiumName(), pageable);

        return all.map(WatchListResponseDto::of);
    }
}
