package com.sparta.spartatigers.domain.chatroom.controller;

import com.sparta.spartatigers.domain.chatroom.dto.request.CreateDirectRoomRequestDto;
import com.sparta.spartatigers.domain.chatroom.dto.response.DirectRoomDto;
import com.sparta.spartatigers.domain.chatroom.service.DirectRoomService;
import com.sparta.spartatigers.domain.user.model.CustomUserPrincipal;
import com.sparta.spartatigers.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

@RestController
@RequestMapping("/api/direct-rooms")
@RequiredArgsConstructor
public class DirectRoomController {

	private final DirectRoomService directRoomService;

	@PostMapping
	public ApiResponse<DirectRoomDto> createDirectRoom(
		@Valid @RequestBody CreateDirectRoomRequestDto request,
		@AuthenticationPrincipal CustomUserPrincipal userPrincipal) {
		Long userId = userPrincipal.getUser().getId();
		DirectRoomDto directRoomDto = directRoomService.createRoom(request.getExchangeRequestId(), userId);
		return ApiResponse.created(directRoomDto);
	}


	@GetMapping
	public ApiResponse<Page<DirectRoomDto>> getDirectRooms(
		@AuthenticationPrincipal CustomUserPrincipal userPrincipal,
		@PageableDefault(size = 5, sort = "createdAt", direction = Sort.Direction.DESC)
		Pageable pageable) {
		Long currentUserId = userPrincipal.getUser().getId();
		Page<DirectRoomDto> rooms = directRoomService.getRoomsForUser(currentUserId, pageable);
		return ApiResponse.ok(rooms);
	}
}
