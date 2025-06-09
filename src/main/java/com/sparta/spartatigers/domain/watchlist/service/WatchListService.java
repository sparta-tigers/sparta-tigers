package com.sparta.spartatigers.domain.watchlist.service;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.sparta.spartatigers.domain.favoriteteam.model.entity.FavoriteTeam;
import com.sparta.spartatigers.domain.favoriteteam.repository.FavoriteTeamRepository;
import com.sparta.spartatigers.domain.match.model.entity.Match;
import com.sparta.spartatigers.domain.match.repository.MatchRepository;
import com.sparta.spartatigers.domain.team.model.entity.Team;
import com.sparta.spartatigers.domain.user.model.CustomUserPrincipal;
import com.sparta.spartatigers.domain.user.model.entity.User;
import com.sparta.spartatigers.domain.user.repository.UserRepository;
import com.sparta.spartatigers.domain.watchlist.dto.request.CreateWatchListRequestDto;
import com.sparta.spartatigers.domain.watchlist.dto.request.SearchWatchListRequestDto;
import com.sparta.spartatigers.domain.watchlist.dto.request.UpdateWatchListRequestDto;
import com.sparta.spartatigers.domain.watchlist.dto.response.CreateWatchListResponseDto;
import com.sparta.spartatigers.domain.watchlist.dto.response.StatsResponseDto;
import com.sparta.spartatigers.domain.watchlist.dto.response.WatchListResponseDto;
import com.sparta.spartatigers.domain.watchlist.model.entity.WatchList;
import com.sparta.spartatigers.domain.watchlist.repository.WatchListRepository;
import com.sparta.spartatigers.global.exception.ExceptionCode;
import com.sparta.spartatigers.global.exception.InvalidRequestException;

@Service
@RequiredArgsConstructor
public class WatchListService {

    private final WatchListRepository watchListRepository;
    private final MatchRepository matchRepository;
    private final FavoriteTeamRepository favoriteTeamRepository;
    private final UserRepository userRepository;

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
        if (match == null) {
            throw new InvalidRequestException(ExceptionCode.MATCH_NOT_FOUND);
        }
        WatchList watchList = WatchList.from(match, request, principal.getUser());

        watchListRepository.save(watchList);

        return CreateWatchListResponseDto.of(watchList);
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

    @Transactional(readOnly = true)
    public StatsResponseDto getStats(CustomUserPrincipal principal) {
        Long userId = CustomUserPrincipal.getUserId(principal);

        User user = userRepository.findByIdOrElseThrow(userId);
        FavoriteTeam favoriteTeam = favoriteTeamRepository.findByUserIdOrElseThrow(userId);
        Team myTeam = favoriteTeam.getTeam();

        List<WatchList> watchLists = watchListRepository.findAllByUser(user);

        int total = watchLists.size();
        int win = 0, draw = 0, lose = 0;

        Map<String, Integer> stadiumVisitCount = new HashMap<>();
        Map<String, int[]> stadiumWdl = new HashMap<>(); // [win, total]

        for (WatchList wl : watchLists) {
            Match match = wl.getMatch();
            if (match.getMatchResult().equals(Match.MatchResult.CANCEL)) {
                continue; // 취소 경기 제외
            }

            // 경기장 방문 집계
            String stadiumName = match.getStadium().getName();
            stadiumVisitCount.put(stadiumName, stadiumVisitCount.getOrDefault(stadiumName, 0) + 1);

            // 경기장별 승리 집계
            stadiumWdl.putIfAbsent(stadiumName, new int[2]);
            int[] wdlArr = stadiumWdl.get(stadiumName);

            // 승/무/패 판정
            Match.MatchResult result = match.getMatchResult();
            boolean isHome = match.getHomeTeam().equals(myTeam);
            boolean isAway = match.getAwayTeam().equals(myTeam);

            // FavoriteTeam이 해당 경기 참가 안함
            if (!isHome && !isAway) {
                continue;
            }

            Match.MatchResult myResult;
            if (isHome) {
                myResult = result;
            } else {
                // Away 기준으로 뒤집기
                if (result == Match.MatchResult.HOME_WIN) myResult = Match.MatchResult.AWAY_WIN;
                else if (result == Match.MatchResult.AWAY_WIN)
                    myResult = Match.MatchResult.HOME_WIN;
                else myResult = result;
            }

            if (myResult == Match.MatchResult.HOME_WIN) { // 내팀이 이김
                win++;
                wdlArr[0]++;
            } else if (myResult == Match.MatchResult.AWAY_WIN) { // 내팀이 짐
                lose++;
            } else if (myResult == Match.MatchResult.DRAW) {
                draw++;
            }
            wdlArr[1]++; // 방문 수
        }

        // 가장 많이 방문한 경기장
        String mostVisitedStadium =
                stadiumVisitCount.entrySet().stream()
                        .max(Map.Entry.comparingByValue())
                        .map(Map.Entry::getKey)
                        .orElse(null);

        // 가장 승률이 좋은 경기장 (3회 이상 방문 기준)
        String bestWinRateStadium =
                stadiumWdl.entrySet().stream()
                        .filter(e -> e.getValue()[1] >= 3)
                        .max(
                                Comparator.comparingDouble(
                                        e -> (double) e.getValue()[0] / e.getValue()[1]))
                        .map(Map.Entry::getKey)
                        .orElse(null);

        double winRate = total > 0 ? (win * 100.0) / total : 0.0;

        return new StatsResponseDto(
                total, winRate, mostVisitedStadium, bestWinRateStadium, win, draw, lose);
    }
}
