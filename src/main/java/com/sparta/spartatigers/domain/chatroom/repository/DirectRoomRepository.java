package com.sparta.spartatigers.domain.chatroom.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.sparta.spartatigers.domain.chatroom.model.entity.DirectRoom;
import com.sparta.spartatigers.domain.exchangerequest.model.entity.ExchangeRequest;

public interface DirectRoomRepository extends JpaRepository<DirectRoom, Long> {

    Optional<DirectRoom> findByExchangeRequest(ExchangeRequest exchangeRequest);

    @Query(
            value =
                    "select dr from direct_rooms dr "
                            + "join fetch dr.sender s "
                            + "join fetch dr.receiver r "
                            + "where dr.sender.id = :userId or dr.receiver.id = :userId",
            countQuery =
                    "select count(dr) from direct_rooms dr "
                            + "where dr.sender.id = :userId or dr.receiver.id = :userId")
    Page<DirectRoom> findBySenderIdOrReceiverIdWithUsers(Long userId, Pageable pageable);

    Optional<DirectRoom> findByExchangeRequestId(Long exchangeRequestId);
}
