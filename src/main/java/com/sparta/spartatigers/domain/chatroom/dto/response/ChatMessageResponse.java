package com.sparta.spartatigers.domain.chatroom.dto.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChatMessageResponse {

	private Long roomId;
	private Long senderId;
	private String senderNickname;
	private String message;
	private LocalDateTime sentAt;
}
