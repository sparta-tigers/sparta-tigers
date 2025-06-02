package com.sparta.spartatigers.domain.match.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sparta.spartatigers.domain.match.model.entity.Match;

public interface MatchRepository extends JpaRepository<Match, Long> {

    // 날짜별 조회
    List<Match> findAllByMatchTimeBetween(LocalDateTime start, LocalDateTime end);
}
