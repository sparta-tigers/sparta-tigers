package com.sparta.spartatigers.domain.chatroom.dto.response;

import com.sparta.spartatigers.domain.chatroom.model.entity.DirectRoom;
import com.sparta.spartatigers.domain.user.model.entity.User;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

	public DirectRoomResponseDto(
		Long directRoomId,
		Long exchangeRequestId,
		Long receiverId,
		String receiverNickname,
		boolean isCompleted,
		LocalDateTime completedAt,
		LocalDateTime createdAt) {
		this.directRoomId = directRoomId;
		this.exchangeRequestId = exchangeRequestId;
		this.receiverId = receiverId;
		this.receiverNickname = receiverNickname;
		this.isCompleted = isCompleted;
		this.completedAt = completedAt;
		this.createdAt = createdAt;
	}

	public static DirectRoomResponseDto from(DirectRoom room, Long currentUserId) {
		User opponent =
			room.getSender().getId().equals(currentUserId)
				? room.getReceiver()
				: room.getSender();

		return new DirectRoomResponseDto(
			room.getId(),
			room.getExchangeRequest().getId(),
			opponent.getId(),
			opponent.getNickname(),
			room.isCompleted(),
			room.getCompletedAt(),
			room.getCreatedAt());
	}
}
