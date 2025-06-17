package com.sparta.spartatigers.domain.user.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class AuthResponseDto {
    private Long id;
    private String path;
    private String email;
    private String nickname;
}
