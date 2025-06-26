package com.sparta.spartatigers.domain.chatroom.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.sparta.spartatigers.domain.chatroom.model.entity.DirectRoom;
import com.sparta.spartatigers.domain.exchangerequest.model.entity.ExchangeRequest;
import com.sparta.spartatigers.domain.item.model.entity.Item;
import com.sparta.spartatigers.domain.user.model.entity.User;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DirectRoomResponseDto {

    private Long directRoomId;
    private Long exchangeRequestId;
    private Long senderId;
    private Long receiverId;
    private LocalDateTime completedAt;
    private LocalDateTime createdAt;
    private boolean isCompleted;
    private String itemTitle;
    private String itemCategory;
    private String itemImage;
    private String opponentNickname;
    private String opponentImage;
    private boolean opponentOnline;

    public static DirectRoomResponseDto from(
            DirectRoom room, Long currentUserId, boolean opponentOnline) {
        ExchangeRequest exchangeRequest = room.getExchangeRequest();
        Item item = exchangeRequest.getItem();

        boolean isSender = room.getSender().getId().equals(currentUserId);
        User opponent = isSender ? room.getReceiver() : room.getSender();

        return new DirectRoomResponseDto(
                room.getId(),
                exchangeRequest.getId(),
                room.getSender().getId(),
                room.getReceiver().getId(),
                room.getCompletedAt(),
                room.getCreatedAt(),
                room.isCompleted(),
                item.getTitle(),
                item.getCategory().name(),
                item.getImage(),
                opponent.getNickname(),
                opponent.getPath(),
                opponentOnline);
    }
}
