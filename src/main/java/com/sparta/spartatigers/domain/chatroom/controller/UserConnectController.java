package com.sparta.spartatigers.domain.chatroom.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import com.sparta.spartatigers.domain.chatroom.service.UserConnectService;
import com.sparta.spartatigers.global.response.ApiResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/direct-rooms/user-connect")
public class UserConnectController {

    private final UserConnectService userConnectService;

    @GetMapping("/{userId}")
    public ApiResponse<Map<String, Object>> userOnlineOrOffline(@PathVariable Long userId) {
        boolean online = userConnectService.isUserOnline(userId);
        Map<String, Object> response =
                Map.of(
                        "userId", userId,
                        "접속여부", online);
        return ApiResponse.ok(response);
    }
}
