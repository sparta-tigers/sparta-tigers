package com.sparta.spartatigers.domain.user.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignUpRequestDto {

    //    @NotBlank(message = "이메일은 필수입니다.")
    //    @Pattern(
    //            regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$",
    //            message = "올바른 이메일 형식이 아닙니다.")
    private String email;

    //    @NotBlank(message = "비밀번호는 필수입니다.")
    //    @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하로 입력해주세요.")
    private String password;

    //    @NotBlank(message = "닉네임은 필수입니다.")
    //    @Size(min = 2, max = 10, message = "닉네임은 2자 이상 10자 이하로 입력해주세요.")
    private String nickname;
}
