package com.sparta.spartatigers.domain.chatroom.controller;

import com.sparta.spartatigers.domain.chatroom.dto.request.CreateDirectRoomRequestDto;
import com.sparta.spartatigers.domain.chatroom.dto.response.DirectRoomDto;
import com.sparta.spartatigers.domain.chatroom.service.DirectRoomService;
import com.sparta.spartatigers.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<ApiResponse<DirectRoomDto>> createDirectRoom(
        @RequestBody CreateDirectRoomRequestDto request) {
        DirectRoomDto directRoomDto = directRoomService.createRoom(request.getExchangeRequestId());
        ApiResponse<DirectRoomDto> response = ApiResponse.created(directRoomDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
