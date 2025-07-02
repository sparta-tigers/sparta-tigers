package com.sparta.spartatigers.domain.favoriteteam.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import com.sparta.spartatigers.domain.favoriteteam.dto.request.FavTeamRequestDto;
import com.sparta.spartatigers.domain.favoriteteam.service.FavoriteTeamService;
import com.sparta.spartatigers.domain.user.model.CustomUserPrincipal;
import com.sparta.spartatigers.global.response.ApiResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class FavoriteTeamController {
    private final FavoriteTeamService favoriteTeamService;

    @PostMapping("/fav")
    public ApiResponse<?> add(
            @RequestBody FavTeamRequestDto request,
            @AuthenticationPrincipal CustomUserPrincipal principal) {
        Long userId = principal.getUser().getId();
        return ApiResponse.created(favoriteTeamService.add(request, userId));
    }

    @GetMapping("/fav")
    public ApiResponse<?> get(@AuthenticationPrincipal CustomUserPrincipal principal) {
        Long userId = principal.getUser().getId();
        return ApiResponse.ok(favoriteTeamService.get(userId));
    }

    @PatchMapping("/fav")
    public ApiResponse<?> update(
            @RequestBody FavTeamRequestDto request,
            @AuthenticationPrincipal CustomUserPrincipal principal) {
        Long userId = principal.getUser().getId();
        return ApiResponse.ok(favoriteTeamService.update(request, userId));
    }

    @DeleteMapping("/fav")
    public ApiResponse<?> delete(@AuthenticationPrincipal CustomUserPrincipal principal) {
        Long userId = principal.getUser().getId();
        return ApiResponse.ok(favoriteTeamService.delete(userId));
    }
}
