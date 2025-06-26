package com.sparta.spartatigers.domain.chatroom.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.sparta.spartatigers.domain.chatroom.model.entity.DirectRoom;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DirectRoomResponseDto {

    private Long directRoomId;
    private Long exchangeRequestId;
    private Long senderId;
    private Long receiverId;
    private String receiverNickname;
    private boolean isCompleted;
    private LocalDateTime completedAt;
    private LocalDateTime createdAt;
    private boolean receiverOnline;

    public static DirectRoomResponseDto from(DirectRoom room, boolean receiverOnline) {
        return new DirectRoomResponseDto(
                room.getId(),
                room.getExchangeRequest().getId(),
                room.getSender().getId(),
                room.getReceiver().getId(),
                room.getReceiver().getNickname(),
                room.isCompleted(),
                room.getCompletedAt(),
                room.getCreatedAt(),
                receiverOnline);
    }
}
