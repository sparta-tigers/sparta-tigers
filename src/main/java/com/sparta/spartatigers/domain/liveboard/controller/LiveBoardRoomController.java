package com.sparta.spartatigers.domain.liveboard.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.sparta.spartatigers.domain.liveboard.dto.response.LiveBoardRoomResponseDto;
import com.sparta.spartatigers.domain.liveboard.service.LiveBoardService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/liveboard")
public class LiveBoardRoomController {

    private final LiveBoardService liveBoardService;

    @GetMapping("/rooms")
    public List<LiveBoardRoomResponseDto> getRooms() {
        List<LiveBoardRoomResponseDto> allRooms = liveBoardService.findAllRoom();
        for (LiveBoardRoomResponseDto allRoom : allRooms) {
            log.info("rooms: {}", allRoom);
        }
        return liveBoardService.findAllRoom();
    }
}
