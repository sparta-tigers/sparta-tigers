package com.sparta.spartatigers.domain.liveboard.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LiveBoardRoomResponseDto {
    private String roomId;
    private String homeTeam;
    private String awayTeam;
    private boolean isClosed;
}
