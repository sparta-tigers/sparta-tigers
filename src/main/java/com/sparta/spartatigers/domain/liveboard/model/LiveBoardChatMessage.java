package com.sparta.spartatigers.domain.liveboard.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LiveBoardChatMessage {

	private String roomId;
	private String sender;
	private String content;
	private LocalDateTime createdAt = LocalDateTime.now();
}
