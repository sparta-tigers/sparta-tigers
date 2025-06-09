package com.sparta.spartatigers.domain.watchlist.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.sparta.spartatigers.domain.user.model.entity.User;
import com.sparta.spartatigers.domain.watchlist.model.entity.WatchList;
import com.sparta.spartatigers.global.exception.ExceptionCode;
import com.sparta.spartatigers.global.exception.InvalidRequestException;

public interface WatchListRepository
        extends JpaRepository<WatchList, Long>, WatchListQueryRepository {

    default WatchList findDetailByIdAndOwnerOrThrow(Long watchListId, Long userId) {
        return findByIdWithMatchDetails(watchListId, userId)
                .orElseThrow(() -> new InvalidRequestException(ExceptionCode.WATCH_LIST_NOT_FOUND));
    }

    @Query(
            """
		SELECT w
		FROM watch_list w
		JOIN FETCH w.match
		JOIN FETCH w.user
		JOIN FETCH w.match.homeTeam
		JOIN FETCH w.match.awayTeam
		""")
    List<WatchList> findAllByUser(User user);
}
