package com.sparta.spartatigers.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateNicknameRequestDto {
    @NotBlank(message = "닉네임은 필수입니다.")
    private String nickname;
}
