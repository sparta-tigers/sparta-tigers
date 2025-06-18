package com.sparta.spartatigers.domain.user.dto.response;

import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AuthResponseDto {
    private String accessToken;

    public static AuthResponseDto from(String token) {
        return new AuthResponseDto(token);
    }
}
