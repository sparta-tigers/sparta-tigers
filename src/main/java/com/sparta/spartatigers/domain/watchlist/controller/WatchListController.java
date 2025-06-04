package com.sparta.spartatigers.domain.watchlist.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import com.sparta.spartatigers.domain.user.model.CustomUserPrincipal;
import com.sparta.spartatigers.domain.watchlist.dto.request.CreateWatchListRequestDto;
import com.sparta.spartatigers.domain.watchlist.dto.response.CreateWatchListResponseDto;
import com.sparta.spartatigers.domain.watchlist.service.WatchListService;
import com.sparta.spartatigers.global.response.ApiResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/watchlist")
public class WatchListController {

    private final WatchListService watchListService;

    /**
     * 직관 기록 등록
     *
     * @return {@link CreateWatchListResponseDto}
     */
    @PostMapping
    public ApiResponse<CreateWatchListResponseDto> create(
            @RequestBody CreateWatchListRequestDto request,
            @AuthenticationPrincipal CustomUserPrincipal principal) {
        return ApiResponse.created(watchListService.create(request, principal));
    }
}
