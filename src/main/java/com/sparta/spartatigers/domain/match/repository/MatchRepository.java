package com.sparta.spartatigers.domain.match.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.sparta.spartatigers.domain.match.model.entity.Match;

public interface MatchRepository extends JpaRepository<Match, Long> {

    // 날짜별 조회
    @Query(
            """
			SELECT m
			FROM matches m
			JOIN FETCH m.homeTeam
			JOIN FETCH m.awayTeam
			JOIN FETCH m.stadium
			WHERE m.matchTime BETWEEN :start AND :end
			""")
    List<Match> findAllByMatchTimeBetween(LocalDateTime start, LocalDateTime end);

    @Query(
            """
			SELECT m
			FROM matches m
			JOIN FETCH m.homeTeam
			JOIN FETCH m.awayTeam
			JOIN FETCH m.stadium
			WHERE m.id = :id
			""")
    Match findByIdWithTeamsAndStadium(Long id);
}
