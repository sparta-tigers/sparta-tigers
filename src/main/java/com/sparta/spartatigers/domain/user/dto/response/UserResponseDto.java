package com.sparta.spartatigers.domain.user.dto.response;

import com.sparta.spartatigers.domain.user.model.entity.User;

public record UserResponseDto(Long userId, String userNickname) {

    public static UserResponseDto from(User user) {
        return new UserResponseDto(user.getId(), user.getNickname());
    }
}
