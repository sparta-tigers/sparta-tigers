package com.sparta.spartatigers.domain.liveboard.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class LiveBoardRoom {

    private String roomId; // 채팅방 접속 구별용? url에 필요할지도?
    private Long matchId;
    private String title;
    private LocalDateTime openAt;
}
