package com.sparta.spartatigers.domain.chatroom.dto.response;

import com.sparta.spartatigers.domain.chatroom.model.entity.DirectRoom;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DirectRoomDto {

    private Long directRoomId;
    private Long exchangeRequestId;
    private Long senderId;
    private Long receiverId;
    private boolean isCompleted;
    private LocalDateTime completedAt;
    private LocalDateTime createdAt;

    public static DirectRoomDto from(DirectRoom room) {
        return new DirectRoomDto(
            room.getId(),
            room.getExchangeRequest().getId(),
            room.getSender().getId(),
            room.getReceiver().getId(),
            room.isCompleted(),
            room.getCompletedAt(),
            room.getCreatedAt()
        );
    }
}
