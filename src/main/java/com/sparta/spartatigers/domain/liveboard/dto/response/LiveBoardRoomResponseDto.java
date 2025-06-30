package com.sparta.spartatigers.domain.liveboard.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import com.sparta.spartatigers.domain.liveboard.model.LiveBoardRoom;
import com.sparta.spartatigers.domain.match.model.entity.Match;
import com.sparta.spartatigers.domain.team.model.entity.Team;

@Builder
@Getter
@AllArgsConstructor
public class LiveBoardRoomResponseDto {

    private String roomId;
    private String title;
    private Long matchId;
    private String awayTeamName;
    private Team.Code awayTeamCode;
    private String homeTeamName;
    private Team.Code homeTeamCode;
    private LocalDateTime startedAt;
    private Match.MatchResult matchResult;
    private String position;
    private Long connectCount;

    public static LiveBoardRoomResponseDto of(LiveBoardRoom room, long connectCount) {
        return builder()
                .roomId(room.getRoomId())
                .title(room.getTitle())
                .startedAt(room.getOpenAt())
                .connectCount(connectCount)
                .build();
        // return new LiveBoardRoomResponseDto(
        //         room.getRoomId(), room.getTitle(), room.getOpenAt(), connectCount);
    }
}
