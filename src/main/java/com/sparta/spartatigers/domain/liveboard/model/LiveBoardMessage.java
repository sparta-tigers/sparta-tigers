package com.sparta.spartatigers.domain.liveboard.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@Getter
@AllArgsConstructor
public class LiveBoardMessage {

    private String roomId; // 채팅방 식별자
    private String senderId;
    private String senderNickname;
    private String content; // 내용
    private LocalDateTime sentAt;
    private MessageType type;

    public LiveBoardMessage() {
        this.sentAt = LocalDateTime.now();
    }
}
