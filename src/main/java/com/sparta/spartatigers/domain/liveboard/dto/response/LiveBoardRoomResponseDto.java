package com.sparta.spartatigers.domain.liveboard.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;

import com.sparta.spartatigers.domain.liveboard.model.LiveBoardRoom;

@Getter
@AllArgsConstructor
public class LiveBoardRoomResponseDto {

    private String roomId;
    private String title;
    private LocalDateTime startedAt;
    private Long connectCount;

    public static LiveBoardRoomResponseDto of(LiveBoardRoom room, long connectCount) {
        return new LiveBoardRoomResponseDto(
                room.getRoomId(), room.getTitle(), room.getOpenAt(), connectCount);
    }
}
