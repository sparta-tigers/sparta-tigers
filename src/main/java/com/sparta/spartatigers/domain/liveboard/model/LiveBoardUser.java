package com.sparta.spartatigers.domain.liveboard.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LiveBoardUser { // 채팅에서 임시로 사용할 유저 객체임,,

	private String sessionId;
	private String userId; // 회원 - 유저 ID, 비회원 - null
	private String nickname;
	private boolean isMember; // 회원, 비회원 여부
	private LocalDateTime connectedAt;

	private String roomId;

	public LiveBoardUser(String sessionId, String userId, String nickname, LocalDateTime connectedAt,
		String roomId) {
		this.sessionId = sessionId;
		this.userId = userId;
		this.nickname = nickname;
		this.connectedAt = connectedAt;
		this.roomId = roomId;
	}
}
