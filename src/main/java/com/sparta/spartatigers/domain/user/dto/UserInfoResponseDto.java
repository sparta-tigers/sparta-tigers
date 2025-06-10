package com.sparta.spartatigers.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserInfoResponseDto {
    private final Long id;
    private final String email;
    private final String providerId;
    private final String nickname;
    private final String profilePath;
    private final String role;
}
