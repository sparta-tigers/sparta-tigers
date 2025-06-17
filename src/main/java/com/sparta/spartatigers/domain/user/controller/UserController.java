package com.sparta.spartatigers.domain.user.controller;

import jakarta.validation.Valid;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import com.sparta.spartatigers.domain.user.dto.request.SignUpRequestDto;
import com.sparta.spartatigers.domain.user.dto.response.UserInfoResponseDto;
import com.sparta.spartatigers.domain.user.model.CustomUserPrincipal;
import com.sparta.spartatigers.domain.user.service.UserService;
import com.sparta.spartatigers.global.response.ApiResponse;
import com.sparta.spartatigers.global.response.MessageCode;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    public ApiResponse<UserInfoResponseDto> getUserInfo(
            @AuthenticationPrincipal CustomUserPrincipal userPrincipal) {
        UserInfoResponseDto userInfoResponseDto = userService.getUserInfo(userPrincipal);
        return ApiResponse.ok(userInfoResponseDto);
    }

    @PostMapping
    public ApiResponse<String> createUser(@RequestBody @Valid SignUpRequestDto signUpRequestDto) {
        userService.createUser(signUpRequestDto);
        return ApiResponse.ok(MessageCode.USER_REQUEST_SUCCESS.getMessage());
    }
}
