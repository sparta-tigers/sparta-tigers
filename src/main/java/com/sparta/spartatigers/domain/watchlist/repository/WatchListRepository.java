package com.sparta.spartatigers.domain.watchlist.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.sparta.spartatigers.domain.watchlist.model.entity.WatchList;

public interface WatchListRepository
        extends JpaRepository<WatchList, Long>, WatchListQueryRepository {

    default WatchList findDetailByIdAndOwnerOrThrow(
            @Param("watchListId") Long watchListId, @Param("userId") Long userId) {
        return findByIdWithMatchDetails(watchListId, userId)
                .orElseThrow(() -> new IllegalArgumentException("기록 식별자가 존재하지 않습니다."));
    }
}
