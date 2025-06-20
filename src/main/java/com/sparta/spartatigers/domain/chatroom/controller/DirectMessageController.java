package com.sparta.spartatigers.domain.chatroom.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import com.sparta.spartatigers.domain.chatroom.dto.response.DirectRoomMessageResponse;
import com.sparta.spartatigers.domain.chatroom.service.DirectMessageService;
import com.sparta.spartatigers.domain.user.model.CustomUserPrincipal;
import com.sparta.spartatigers.global.response.ApiResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/direct-rooms")
public class DirectMessageController {

    private final DirectMessageService directMessageService;

    @GetMapping("/{roomId}/messages")
    public ApiResponse<Page<DirectRoomMessageResponse>> getMessages(
            @AuthenticationPrincipal CustomUserPrincipal userPrincipal,
            @PathVariable Long roomId,
            @PageableDefault(
                            size = 20,
                            sort = {"sentAt", "id"},
                            direction = Sort.Direction.DESC)
                    Pageable pageable) {

        Long currentUserId = userPrincipal.getUser().getId();

        Page<DirectRoomMessageResponse> messages =
                directMessageService.getMessages(roomId, currentUserId, pageable);

        return ApiResponse.ok(messages);
    }
}
