package com.sparta.spartatigers.domain.liveboard.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LiveBoardMessage {

    private String roomId; // 채팅방 식별자
    private Long senderId;
    private String senderNickName;
    private String content; // 내용
    private LocalDateTime sentAt;
    private MessageType messageType;

    public static LiveBoardMessage of(
            String roomId, Long senderId, String senderNickname, String content) {
        return new LiveBoardMessage(
                roomId, senderId, senderNickname, content, LocalDateTime.now(), MessageType.CHAT);
    }

    public static LiveBoardMessage of(
            String roomId,
            Long senderId,
            String senderNickname,
            String content,
            MessageType messageType) {
        return new LiveBoardMessage(
                roomId, senderId, senderNickname, content, LocalDateTime.now(), messageType);
    }
}
