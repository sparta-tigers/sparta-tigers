package com.sparta.spartatigers.domain.user.controller;

import jakarta.validation.Valid;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import com.sparta.spartatigers.domain.user.dto.request.LoginRequestDto;
import com.sparta.spartatigers.domain.user.dto.request.SignUpRequestDto;
import com.sparta.spartatigers.domain.user.dto.request.UpdateNicknameRequestDto;
import com.sparta.spartatigers.domain.user.dto.request.UpdatePasswordRequestDto;
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
        if (userPrincipal == null) {
            return ApiResponse.ok(null);
        }

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
    @PatchMapping("/profile")
    public ApiResponse<ProfileResponseDto> updateUserImage(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal CustomUserPrincipal userPrincipal) {
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

    @PatchMapping("/nickname")
    public ApiResponse<String> updateNickname(
            @RequestBody UpdateNicknameRequestDto updateNicknameRequestDto,
            @AuthenticationPrincipal CustomUserPrincipal userPrincipal) {
        Long userId = userPrincipal.getUser().getId();
        userService.updateNickname(updateNicknameRequestDto, userId);
        return ApiResponse.ok(MessageCode.USER_NICKNAME_UPDATE_SUCCESS.getMessage());
    }

    @PatchMapping("/password")
    public ApiResponse<String> updatePassword(
            @RequestBody UpdatePasswordRequestDto updatePasswordRequestDto,
            @AuthenticationPrincipal CustomUserPrincipal userPrincipal) {
        Long userId = userPrincipal.getUser().getId();
        userService.updatePassword(updatePasswordRequestDto, userId);
        return ApiResponse.ok(MessageCode.USER_PASSWORD_UPDATE_SUCCESS.getMessage());
    }

    @DeleteMapping
    public ApiResponse<String> deleteUser(
            @AuthenticationPrincipal CustomUserPrincipal userPrincipal) {
        Long userId = userPrincipal.getUser().getId();
        userService.deleteUser(userId);
        return ApiResponse.ok(MessageCode.USER_DELETED.getMessage());
    }
}
