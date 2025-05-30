package com.sparta.spartatigers.domain.chatroom.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sparta.spartatigers.domain.chatroom.model.entity.DirectRoom;
import com.sparta.spartatigers.domain.exchangerequest.model.entity.ExchangeRequest;

public interface DirectRoomRepository extends JpaRepository<DirectRoom, Long> {

    Optional<DirectRoom> findByExchangeRequest(ExchangeRequest exchangeRequest);
}
