package com.sparta.spartatigers.domain.chatroom.config;

import java.security.Principal;

public class StompPrincipal implements Principal {

    private final String name;

    public StompPrincipal(Long userId, String nickname) {
        this.name = String.valueOf(userId);
    }

    @Override
    public String getName() {
        return name;
    }
}
