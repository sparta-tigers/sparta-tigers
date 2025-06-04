package com.sparta.spartatigers.domain.chatroom.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sparta.spartatigers.domain.chatroom.model.entity.DirectRoom;
import com.sparta.spartatigers.domain.exchangerequest.model.entity.ExchangeRequest;
import com.sparta.spartatigers.domain.user.model.entity.User;

public interface DirectRoomRepository extends JpaRepository<DirectRoom, Long> {

    Optional<DirectRoom> findByExchangeRequest(ExchangeRequest exchangeRequest);

    @Query("SELECT r FROM direct_rooms r WHERE r.sender = :user OR r.receiver = :user")
    Page<DirectRoom> findAllByUser(@Param("user") User user, Pageable pageable);
}
