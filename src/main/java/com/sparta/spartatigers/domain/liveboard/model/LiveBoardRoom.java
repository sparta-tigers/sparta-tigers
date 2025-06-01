package com.sparta.spartatigers.domain.liveboard.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LiveBoardRoom {

    private String roomId; // 채팅방 접속 구별용? url에 필요할지도?
    private Long matchId;
    private String title;
    private LocalDateTime openAt;
    private LocalDateTime closedAt;
    private boolean isClosed;
    private int connectCount;

    public void close() {
        this.isClosed = true;
    }

    public void increaseCount() {
        this.connectCount++;
    }

    public void decreaseCount() {
        if (this.connectCount > 0) {
            this.connectCount--;
        }
    }
}
