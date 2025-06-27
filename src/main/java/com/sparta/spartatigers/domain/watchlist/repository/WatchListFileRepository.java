package com.sparta.spartatigers.domain.watchlist.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sparta.spartatigers.domain.watchlist.model.entity.WatchListFile;

public interface WatchListFileRepository extends JpaRepository<WatchListFile, Long> {
    List<WatchListFile> findAllByFileUrlIn(List<String> urls);
}
