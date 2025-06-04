package com.sparta.spartatigers.domain.watchlist.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

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
		WHERE m.id = :id
		""")
    Page<WatchList> findAllByMatchId(Pageable pageable, Long id);
}
