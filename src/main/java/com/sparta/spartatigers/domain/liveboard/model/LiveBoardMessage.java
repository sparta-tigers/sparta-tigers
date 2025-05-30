package com.sparta.spartatigers.domain.liveboard.model;

import java.time.LocalDateTime;

import org.springframework.cglib.core.Local;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LiveBoardMessage {

	private String roomId; // 채팅방 식별자
	private String senderId;
	private String senderNickname;
	private String content; // 내용
	private LocalDateTime sentAt = LocalDateTime.now();


}
