package com.sparta.spartatigers.domain.chatroom.controller;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import com.sparta.spartatigers.domain.chatroom.dto.request.CreateDirectRoomRequestDto;
import com.sparta.spartatigers.domain.chatroom.dto.response.DirectRoomDto;
import com.sparta.spartatigers.domain.chatroom.service.DirectRoomService;
import com.sparta.spartatigers.domain.user.model.entity.User;
import com.sparta.spartatigers.global.response.ApiResponse;

@RestController
@RequestMapping("/api/direct-rooms")
@RequiredArgsConstructor
public class DirectRoomController {

    private final DirectRoomService directRoomService;

    @PostMapping
    public ApiResponse<DirectRoomDto> createDirectRoom(
            @Valid @RequestBody CreateDirectRoomRequestDto request) {
        DirectRoomDto directRoomDto = directRoomService.createRoom(request.getExchangeRequestId());
        return ApiResponse.created(directRoomDto);
    }

    @GetMapping
    public ApiResponse<Page<DirectRoomDto>> getDirectRooms(
            @AuthenticationPrincipal User currentUser,
            @PageableDefault(size = 5, sort = "createdAt", direction = Sort.Direction.DESC)
                    Pageable pageable) {
        Page<DirectRoomDto> roomPage = directRoomService.getRoomsForUser(currentUser, pageable);
        return ApiResponse.ok(roomPage);
    }
}
