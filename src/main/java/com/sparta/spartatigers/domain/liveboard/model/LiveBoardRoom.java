package com.sparta.spartatigers.domain.liveboard.model;

import java.time.LocalDateTime;

import com.sparta.spartatigers.domain.common.entity.BaseEntity;
import com.sparta.spartatigers.domain.match.model.entity.Match;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class LiveBoardRoom {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String roomId; // 채팅방 접속 구별용? url에 필요할지도?
	@ManyToOne
	@JoinColumn(name= "match_id")
	private Match match;
	private String title;
	private LocalDateTime startedAt;
	private LocalDateTime closedAt;
	private boolean isClosed;

	public LiveBoardRoom(String roomId, Match match, String title, LocalDateTime startedAt, LocalDateTime closedAt) {
		this.roomId = roomId;
		this.match = match;
		this.title = title;
		this.startedAt = startedAt;
		this.closedAt = closedAt;
		this.isClosed = false;
	}

	public void close() {
		this.isClosed = true;
	}
}
