package com.sparta.spartatigers.domain.chatroom.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.sparta.spartatigers.domain.chatroom.model.entity.DirectRoom;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DirectRoomCreateResponseDto {

    private Long directRoomId;
    private Long exchangeRequestId;
    private Long senderId;
    private Long receiverId;
    private String receiverNickname;
    private boolean isCompleted;
    private LocalDateTime completedAt;
    private LocalDateTime createdAt;

    public static DirectRoomCreateResponseDto from(DirectRoom room) {
        return new DirectRoomCreateResponseDto(
                room.getId(),
                room.getExchangeRequest().getId(),
                room.getSender().getId(),
                room.getReceiver().getId(),
                room.getReceiver().getNickname(),
                room.isCompleted(),
                room.getCompletedAt(),
                room.getCreatedAt());
    }
}
