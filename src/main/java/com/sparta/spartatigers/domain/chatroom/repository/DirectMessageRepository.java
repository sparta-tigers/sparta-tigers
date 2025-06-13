package com.sparta.spartatigers.domain.chatroom.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sparta.spartatigers.domain.chatroom.model.entity.DirectMessage;

public interface DirectMessageRepository extends JpaRepository<DirectMessage, Long> {

    @Query("SELECT m FROM direct_message m JOIN FETCH m.sender WHERE m.directRoom.id = :roomId")
    Page<DirectMessage> findByDirectRoomIdWithSender(
            @Param("roomId") Long roomId, Pageable pageable);
}
