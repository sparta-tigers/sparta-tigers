package com.sparta.spartatigers.domain.chatroom.controller;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import com.sparta.spartatigers.domain.chatroom.dto.request.CreateDirectRoomRequestDto;
import com.sparta.spartatigers.domain.chatroom.dto.response.DirectRoomResponseDto;
import com.sparta.spartatigers.domain.chatroom.service.DirectRoomService;
import com.sparta.spartatigers.domain.user.model.CustomUserPrincipal;
import com.sparta.spartatigers.global.response.ApiResponse;

@RestController
@RequestMapping("/api/direct-rooms")
@RequiredArgsConstructor
public class DirectRoomController {

    private final DirectRoomService directRoomService;

    @PostMapping
    public ApiResponse<DirectRoomResponseDto> createDirectRoom(
            @Valid @RequestBody CreateDirectRoomRequestDto request,
            @AuthenticationPrincipal CustomUserPrincipal userPrincipal) {
        Long userId = userPrincipal.getUser().getId();
        DirectRoomResponseDto directRoomDto =
                directRoomService.createRoom(request.getExchangeRequestId(), userId);
        return ApiResponse.created(directRoomDto);
    }

    @GetMapping
    public ApiResponse<Page<DirectRoomResponseDto>> getDirectRooms(
            @AuthenticationPrincipal CustomUserPrincipal userPrincipal,
            @PageableDefault(size = 5, sort = "createdAt", direction = Sort.Direction.DESC)
                    Pageable pageable) {
        Long currentUserId = userPrincipal.getUser().getId();
        Page<DirectRoomResponseDto> rooms =
                directRoomService.getRoomsForUser(currentUserId, pageable);
        return ApiResponse.ok(rooms);
    }

    @DeleteMapping("/{directRoomId}")
    public ApiResponse<String> deleteDirectRoom(
            @PathVariable Long directRoomId,
            @AuthenticationPrincipal CustomUserPrincipal userPrincipal) {

        Long currentUserId = userPrincipal.getUser().getId();
        directRoomService.deleteRoom(directRoomId, currentUserId);
        return ApiResponse.ok("채팅방이 정상적으로 삭제되었습니다!");
    }
}
