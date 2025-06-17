package com.sparta.spartatigers.domain.liveboard.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LiveBoardMessage {

    private String roomId; // 채팅방 식별자
    private Long senderId;
    private String senderNickName;
    private String content; // 내용
    private LocalDateTime sentAt;

    public static LiveBoardMessage of(
            String roomId, Long senderId, String senderNickname, String content) {
        return new LiveBoardMessage(roomId, senderId, content, senderNickname, LocalDateTime.now());
    }
}
