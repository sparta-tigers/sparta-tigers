package com.sparta.spartatigers.domain.item.dto.response;

import com.sparta.spartatigers.domain.user.model.entity.User;

public record UserDto(Long userId, String userNickname) {

    public static UserDto from(User user) {
        return new UserDto(user.getId(), user.getNickname());
    }
}
