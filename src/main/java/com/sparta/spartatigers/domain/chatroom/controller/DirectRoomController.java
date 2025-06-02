package com.sparta.spartatigers.domain.chatroom.controller;

import com.sparta.spartatigers.domain.chatroom.dto.request.CreateDirectRoomRequestDto;
import com.sparta.spartatigers.domain.chatroom.dto.response.DirectRoomDto;
import com.sparta.spartatigers.domain.chatroom.service.DirectRoomService;
import com.sparta.spartatigers.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
		@Valid @RequestBody CreateDirectRoomRequestDto request) {
		DirectRoomDto directRoomDto = directRoomService.createRoom(request.getExchangeRequestId());
		return ApiResponse.created(directRoomDto);
	}
}
