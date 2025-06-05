package com.sparta.spartatigers.domain.watchlist.repository;

import com.sparta.spartatigers.global.exception.ExceptionCode;
import com.sparta.spartatigers.global.exception.InvalidRequestException;
import org.springframework.data.jpa.repository.JpaRepository;

import com.sparta.spartatigers.domain.watchlist.model.entity.WatchList;

public interface WatchListRepository
        extends JpaRepository<WatchList, Long>, WatchListQueryRepository {

    default WatchList findDetailByIdAndOwnerOrThrow(Long watchListId, Long userId) {
        return findByIdWithMatchDetails(watchListId, userId)
                .orElseThrow(() -> new InvalidRequestException(ExceptionCode.WATCH_LIST_NOT_FOUN));
    }
}
