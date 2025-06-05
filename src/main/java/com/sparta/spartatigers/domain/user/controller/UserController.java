package com.sparta.spartatigers.domain.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import com.sparta.spartatigers.domain.user.dto.UserInfoResponseDto;
import com.sparta.spartatigers.domain.user.model.CustomUserPrincipal;
import com.sparta.spartatigers.domain.user.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserInfoResponseDto> getUserInfo(
            @AuthenticationPrincipal CustomUserPrincipal userPrincipal) {

        UserInfoResponseDto userInfoResponseDto = userService.getUserInfo(userPrincipal);
        return ResponseEntity.ok(userInfoResponseDto);
    }
}
