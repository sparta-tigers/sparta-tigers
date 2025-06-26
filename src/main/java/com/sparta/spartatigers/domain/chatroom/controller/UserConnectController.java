package com.sparta.spartatigers.domain.chatroom.controller;

import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import com.sparta.spartatigers.domain.chatroom.service.UserConnectService;
import com.sparta.spartatigers.domain.user.model.CustomUserPrincipal;
import com.sparta.spartatigers.global.response.ApiResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/direct-rooms")
public class UserConnectController {

    private final UserConnectService userConnectService;

    @GetMapping("/{roomId}/user-connect")
    public ApiResponse<Map<String, Object>> getOpponentConnectionStatus(
            @AuthenticationPrincipal CustomUserPrincipal userPrincipal, @PathVariable Long roomId) {
        Long requesterId = userPrincipal.getUser().getId();

        // 상대방의 접속 여부 확인
        boolean isOpponentOnline = userConnectService.isOpponentOnlineInRoom(requesterId, roomId);

        Map<String, Object> response =
                Map.of(
                        "roomId", roomId,
                        "isOpponentOnline", isOpponentOnline);

        return ApiResponse.ok(response);
    }
}
