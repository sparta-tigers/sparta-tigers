package com.sparta.spartatigers.domain.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import com.sparta.spartatigers.domain.user.model.entity.User;

@Getter
@AllArgsConstructor
public class UserInfoResponseDto {
    private final Long id;
    private final String email;
    private final String nickname;
    private final String path;

    public static UserInfoResponseDto from(User user) {
        return new UserInfoResponseDto(
                user.getId(), user.getEmail(), user.getNickname(), user.getPath());
    }
}
