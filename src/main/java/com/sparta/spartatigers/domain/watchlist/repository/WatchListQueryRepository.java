package com.sparta.spartatigers.domain.watchlist.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.sparta.spartatigers.domain.watchlist.model.entity.WatchList;

public interface WatchListQueryRepository {

    /**
     * userId를 가진 사용자가 작성한 직관 기록 목록을 페이지로 가져오는 메서드
     *
     * @param userId 유저 식별자
     * @param pageable 페이지 정보
     * @return {@link Page<WatchList>}
     */
    Page<WatchList> findAllByUserIdWithMatchDetails(Long userId, Pageable pageable);

    /**
     * userId를 가진 사용자가 작성한 직관 기록 목록 중 watchListId의 직관 기록을 가져오는 메서드
     *
     * @param watchListId 직관 기록 식별자
     * @param userId 유저 식별자
     * @return {@link Optional<WatchList>}
     */
    Optional<WatchList> findByIdWithMatchDetails(Long watchListId, Long userId);
}
