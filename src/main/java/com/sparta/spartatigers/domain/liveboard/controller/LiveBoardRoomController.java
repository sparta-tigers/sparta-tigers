package com.sparta.spartatigers.domain.liveboard.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import com.sparta.spartatigers.domain.liveboard.dto.response.LiveBoardRoomResponseDto;
import com.sparta.spartatigers.domain.liveboard.service.LiveBoardRoomService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/liveboard/room")
public class LiveBoardRoomController {

    private final LiveBoardRoomService liveBoardRoomService;

    @PostMapping
    public List<LiveBoardRoomResponseDto> createTodayRoom() {
        return liveBoardRoomService.createTodayRoom();
    }

    @GetMapping("/all")
    public List<LiveBoardRoomResponseDto> getAllRooms() {
        return liveBoardRoomService.findAllRooms();
    }

    @GetMapping("/today")
    public List<LiveBoardRoomResponseDto> getTodayRooms() {
        return liveBoardRoomService.findTodayRooms();
    }

    @DeleteMapping("/{roomId}")
    public void deleteRoom(@PathVariable String roomId) {
        liveBoardRoomService.deleteRoom(roomId);
    }
}
