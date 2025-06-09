package com.sparta.spartatigers.domain.chatroom.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageRequest {

    private Long roomId;
    private Long senderId;
    private Long receiverId;
    private String message;
}
