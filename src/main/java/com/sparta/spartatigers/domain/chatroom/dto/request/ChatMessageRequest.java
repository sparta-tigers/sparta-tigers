package com.sparta.spartatigers.domain.chatroom.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChatMessageRequest {

	private Long roomId;
	private Long senderId;

	@NotNull
	private String message;

}
