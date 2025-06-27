package com.sparta.spartatigers.domain.chatroom.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import com.sparta.spartatigers.domain.chatroom.registry.RedisUserSessionRegistry;

@Service
@RequiredArgsConstructor
public class UserConnectService {

    private final RedisUserSessionRegistry userSessionRegistry;

    public boolean isUserOnline(Long userId) {
        return userSessionRegistry.isUserConnected(userId);
    }
}
