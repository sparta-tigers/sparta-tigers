package com.sparta.spartatigers.domain.watchlist.controller;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import com.sparta.spartatigers.domain.user.model.CustomUserPrincipal;
import com.sparta.spartatigers.domain.watchlist.dto.request.CreateWatchListRequestDto;
import com.sparta.spartatigers.domain.watchlist.dto.request.SearchWatchListRequestDto;
import com.sparta.spartatigers.domain.watchlist.dto.request.UpdateWatchListRequestDto;
import com.sparta.spartatigers.domain.watchlist.dto.response.CreateWatchListImageResponseDto;
import com.sparta.spartatigers.domain.watchlist.dto.response.CreateWatchListResponseDto;
import com.sparta.spartatigers.domain.watchlist.dto.response.StatsResponseDto;
import com.sparta.spartatigers.domain.watchlist.dto.response.WatchListResponseDto;
import com.sparta.spartatigers.domain.watchlist.service.WatchListService;
import com.sparta.spartatigers.global.response.ApiResponse;
import com.sparta.spartatigers.global.response.MessageCode;

@Log4j2
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
            @Valid @RequestBody CreateWatchListRequestDto request,
            @AuthenticationPrincipal CustomUserPrincipal principal) {
        return ApiResponse.created(watchListService.create(request, principal.getUser()));
    }

    /*
    직관 기록 이미지 등록
     */
    @PostMapping("/uploads")
    public ApiResponse<CreateWatchListImageResponseDto> uploadImage(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal CustomUserPrincipal principal) {
        Long userId = principal.getUser().getId();
        CreateWatchListImageResponseDto responseDto = watchListService.upload(file, userId);
        return ApiResponse.ok(responseDto);
    }

    /**
     * 직관 기록 다건 조회
     *
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @param principal 유저 정보
     * @return {@link WatchListResponseDto}
     */
    @GetMapping
    public ApiResponse<Page<WatchListResponseDto>> findAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal CustomUserPrincipal principal) {
        Pageable pageable = PageRequest.of(page - 1, size);
        return ApiResponse.ok(
                watchListService.findAll(pageable, CustomUserPrincipal.getUserId(principal)));
    }

    /**
     * 직관 기록 단건 조회
     *
     * @param watchListId 직관 기록 식별자
     * @param principal 유저 정보
     * @return {@link WatchListResponseDto}
     */
    @GetMapping("/{watchListId}")
    public ApiResponse<WatchListResponseDto> findOne(
            @PathVariable Long watchListId,
            @AuthenticationPrincipal CustomUserPrincipal principal) {
        return ApiResponse.ok(
                watchListService.findOne(watchListId, CustomUserPrincipal.getUserId(principal)));
    }

    /**
     * 직관 기록 수정
     *
     * @param watchListId 직관 기록 식별자
     * @param request 요청 DTO {@link UpdateWatchListRequestDto}
     * @param principal 유저 정보
     * @return {@link WatchListResponseDto}
     */
    @PatchMapping("/{watchListId}")
    public ApiResponse<WatchListResponseDto> update(
            @PathVariable Long watchListId,
            @Valid @RequestBody UpdateWatchListRequestDto request,
            @AuthenticationPrincipal CustomUserPrincipal principal) {
        return ApiResponse.ok(
                watchListService.update(
                        watchListId, request, CustomUserPrincipal.getUserId(principal)));
    }

    /**
     * 직관 기록 삭제
     *
     * @param watchListId 직관 기록 식별자
     * @param principal 유저 정보
     * @return String
     */
    @DeleteMapping("/{watchListId}")
    public ApiResponse<String> delete(
            @PathVariable Long watchListId,
            @AuthenticationPrincipal CustomUserPrincipal principal) {
        watchListService.delete(watchListId, CustomUserPrincipal.getUserId(principal));
        return ApiResponse.ok(MessageCode.WATCH_LIST_DELETED.getMessage());
    }

    /**
     * 직관 기록 검색
     *
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @param request 요청 DTO {@link SearchWatchListRequestDto}
     * @param principal 유저 정보
     * @return {@link Page<WatchListResponseDto>}
     */
    @PostMapping("/search")
    public ApiResponse<Page<WatchListResponseDto>> search(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestBody SearchWatchListRequestDto request,
            @AuthenticationPrincipal CustomUserPrincipal principal) {
        Pageable pageable = PageRequest.of(page - 1, size);
        return ApiResponse.ok(
                watchListService.search(
                        pageable, request, CustomUserPrincipal.getUserId(principal)));
    }

    /**
     * 회원인 유저가 등록한 직관 통계 조회
     *
     * @param principal 유저 정보
     * @return {@link StatsResponseDto}
     */
    @GetMapping("/stats")
    public ApiResponse<StatsResponseDto> getStats(
            @AuthenticationPrincipal CustomUserPrincipal principal) {
        return ApiResponse.ok(watchListService.getStats(CustomUserPrincipal.getUserId(principal)));
    }
}
