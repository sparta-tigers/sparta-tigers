package com.sparta.spartatigers.domain.user.controller;

import jakarta.validation.Valid;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import com.sparta.spartatigers.domain.user.dto.request.LoginRequestDto;
import com.sparta.spartatigers.domain.user.dto.request.SignUpRequestDto;
import com.sparta.spartatigers.domain.user.dto.response.AuthResponseDto;
import com.sparta.spartatigers.domain.user.dto.response.ProfileResponseDto;
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

    // 일반 로그인
    @PostMapping("/login")
    public ApiResponse<AuthResponseDto> login(@RequestBody @Valid LoginRequestDto loginRequestDto) {
        AuthResponseDto authResDTO = userService.login(loginRequestDto);
        return ApiResponse.ok(authResDTO);
    }

    // 내 정보 조회
    @GetMapping("/me")
    public ApiResponse<UserInfoResponseDto> getUserInfo(
            @AuthenticationPrincipal CustomUserPrincipal userPrincipal) {
        Long userId = userPrincipal.getUser().getId();
        UserInfoResponseDto userInfoResponseDto = userService.getUserInfo(userId);
        return ApiResponse.ok(userInfoResponseDto);
    }

    // 회원가입
    @PostMapping("/signup")
    public ApiResponse<String> createUser(@RequestBody @Valid SignUpRequestDto signUpRequestDto) {
        userService.createUser(signUpRequestDto);
        return ApiResponse.ok(MessageCode.USER_REQUEST_SUCCESS.getMessage());
    }

    /*
    유저 이미지 수정,
    그리고 S3 내 이전 이미지 삭제
     */
    @PutMapping("/profile")
    public ApiResponse<ProfileResponseDto> updateUserImage(
            @RequestParam("file") MultipartFile file, @AuthenticationPrincipal CustomUserPrincipal userPrincipal) {
        Long userId = userPrincipal.getUser().getId();
        return ApiResponse.ok(userService.updateProfile(file, userId));
    }

    /*
    유저 이미지 삭제, 삭제 시 default 이미지로 변경된다.
    그리고 S3 내 이전 이미지 삭제
     */
    @DeleteMapping("/profile")
    public ApiResponse<String> deleteUserImage(
            @AuthenticationPrincipal CustomUserPrincipal userPrincipal) {
        Long userId = userPrincipal.getUser().getId();
        userService.deleteProfile(userId);
		return ApiResponse.ok(MessageCode.PROFILE_DELETE_SUCCESS.getMessage());
    }
}
