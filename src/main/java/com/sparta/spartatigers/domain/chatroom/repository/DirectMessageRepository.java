package com.sparta.spartatigers.domain.chatroom.repository;

import com.sparta.spartatigers.domain.chatroom.model.entity.DirectMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DirectMessageRepository extends JpaRepository<DirectMessage, Long> {

}
