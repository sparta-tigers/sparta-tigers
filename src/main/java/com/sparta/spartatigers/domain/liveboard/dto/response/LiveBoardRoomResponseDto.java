package com.sparta.spartatigers.domain.liveboard.dto.response;

import com.sparta.spartatigers.domain.liveboard.model.LiveBoard;
import com.sparta.spartatigers.domain.liveboard.model.LiveBoardRoom;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LiveBoardRoomResponseDto {
    private String roomId;
    private String homeTeam;
    private String awayTeam;
    private boolean isClosed;

	public static LiveBoardRoomResponseDto of(LiveBoardRoom room) {
		return new LiveBoardRoomResponseDto(
			room.getRoomId(),
			room.getMatch().getHomeTeam().getName(),
			room.getMatch().getAwayTeam().getName(),
			room.isClosed()
		);
	}
}
