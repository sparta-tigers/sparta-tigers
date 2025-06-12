package com.sparta.spartatigers.domain.chatroom.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sparta.spartatigers.domain.chatroom.model.entity.DirectMessage;

public interface DirectMessageRepository extends JpaRepository<DirectMessage, Long> {}
