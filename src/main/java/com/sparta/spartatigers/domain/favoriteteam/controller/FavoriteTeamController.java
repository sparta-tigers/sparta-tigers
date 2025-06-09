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

import com.sparta.spartatigers.domain.favoriteteam.dto.request.AddFavTeamRequestDto;
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
            @RequestBody AddFavTeamRequestDto request,
            @AuthenticationPrincipal CustomUserPrincipal principal) {
        return ApiResponse.created(favoriteTeamService.add(request, principal));
    }

    @GetMapping("/fav")
    public ApiResponse<?> get(@AuthenticationPrincipal CustomUserPrincipal principal) {
        return ApiResponse.ok(favoriteTeamService.get(principal));
    }

    @PatchMapping("/fav")
    public ApiResponse<?> update(
            @RequestBody AddFavTeamRequestDto request,
            @AuthenticationPrincipal CustomUserPrincipal principal) {
        return ApiResponse.ok(favoriteTeamService.update(request, principal));
    }

    @DeleteMapping("/fav")
    public ApiResponse<?> delete(@AuthenticationPrincipal CustomUserPrincipal principal) {
        return ApiResponse.ok(favoriteTeamService.delete(principal));
    }
}
