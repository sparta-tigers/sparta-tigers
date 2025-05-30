package com.sparta.spartatigers.domain.liveboard.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sparta.spartatigers.domain.liveboard.model.LiveBoardRoom;

public interface LiveBoardRoomRepository extends JpaRepository<LiveBoardRoom, Long> {

}
