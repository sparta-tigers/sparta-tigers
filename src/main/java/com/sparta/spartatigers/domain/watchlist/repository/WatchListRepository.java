package com.sparta.spartatigers.domain.watchlist.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sparta.spartatigers.domain.watchlist.model.entity.WatchList;

public interface WatchListRepository extends JpaRepository<WatchList, Long> {}
