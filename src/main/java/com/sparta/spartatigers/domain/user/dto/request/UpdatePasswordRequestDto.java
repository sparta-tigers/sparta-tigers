package com.sparta.spartatigers.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class UpdatePasswordRequestDto {
    @NotBlank(message = "기존 비밀번호는 필수입니다.")
    private String oldPassword;

    @NotBlank(message = "새 비밀번호는 필수입니다.")
    private String newPassword;
}
