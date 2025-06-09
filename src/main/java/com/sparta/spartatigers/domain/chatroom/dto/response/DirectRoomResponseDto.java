package com.sparta.spartatigers.domain.chatroom.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.sparta.spartatigers.domain.chatroom.model.entity.DirectRoom;
import com.sparta.spartatigers.domain.user.model.entity.User;

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

    public static DirectRoomResponseDto from(DirectRoom room, Long currentUserId) {
        User opponent =
                room.getSender().getId().equals(currentUserId)
                        ? room.getReceiver()
                        : room.getSender();

        return new DirectRoomResponseDto(
                room.getId(),
                room.getExchangeRequest().getId(),
                room.getSender().getId(),
                opponent.getId(),
                opponent.getNickname(),
                room.isCompleted(),
                room.getCompletedAt(),
                room.getCreatedAt());
    }
}
