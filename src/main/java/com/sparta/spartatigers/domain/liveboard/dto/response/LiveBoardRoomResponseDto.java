package com.sparta.spartatigers.domain.liveboard.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import com.sparta.spartatigers.domain.liveboard.model.LiveBoardRoom;

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
                room.isClosed());
    }
}
