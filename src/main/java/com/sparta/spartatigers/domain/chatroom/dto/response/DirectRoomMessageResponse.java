package com.sparta.spartatigers.domain.chatroom.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;

import com.sparta.spartatigers.domain.chatroom.model.entity.DirectMessage;

@Getter
@AllArgsConstructor
public class DirectRoomMessageResponse {

    private Long messageId;
    private Long senderId;
    private String senderNickname;
    private String message;
    private LocalDateTime sentAt;

    public static DirectRoomMessageResponse from(DirectMessage message) {
        return new DirectRoomMessageResponse(
                message.getId(),
                message.getSender().getId(),
                message.getSender().getNickname(),
                message.getMessage(),
                message.getSentAt());
    }
}
