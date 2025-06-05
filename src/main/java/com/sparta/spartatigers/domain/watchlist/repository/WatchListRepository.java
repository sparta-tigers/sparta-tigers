package com.sparta.spartatigers.domain.watchlist.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sparta.spartatigers.domain.watchlist.model.entity.WatchList;

public interface WatchListRepository extends JpaRepository<WatchList, Long> {

    @Query(
            """
		SELECT w
		FROM watch_list w
		JOIN FETCH w.match m
		JOIN FETCH w.match.awayTeam at
		JOIN FETCH w.match.homeTeam ht
		JOIN FETCH w.match.stadium s
		WHERE w.user.id = :userId
		""")
    Page<WatchList> findAllByUserIdWithMatchDetails(
            @Param("userId") Long userId, Pageable pageable);

    @Query(
            """
		SELECT w
		FROM watch_list w
		JOIN FETCH w.match m
		JOIN FETCH w.match.awayTeam at
		JOIN FETCH w.match.homeTeam ht
		JOIN FETCH w.match.stadium s
		WHERE w.id = :watchListId AND w.user.id = :userId
		""")
    Optional<WatchList> findByIdWithMatchDetails(
            @Param("watchListId") Long watchListId, @Param("userId") Long userId);

    default WatchList findByIdOrElseThrow(
            @Param("watchListId") Long watchListId, @Param("userId") Long userId) {
        return findByIdWithMatchDetails(watchListId, userId)
                .orElseThrow(() -> new IllegalArgumentException("기록 식별자가 존재하지 않습니다."));
    }
}
