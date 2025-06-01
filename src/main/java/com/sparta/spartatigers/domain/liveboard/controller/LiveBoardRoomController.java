package com.sparta.spartatigers.domain.liveboard.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import com.sparta.spartatigers.domain.liveboard.dto.response.LiveBoardRoomResponseDto;
import com.sparta.spartatigers.domain.liveboard.service.LiveBoardService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/liveboard")
public class LiveBoardRoomController {

    private final LiveBoardService liveBoardService;

    @GetMapping("/rooms")
    public List<LiveBoardRoomResponseDto> getRooms() {
        return liveBoardService.findAllRoom();
    }
}
