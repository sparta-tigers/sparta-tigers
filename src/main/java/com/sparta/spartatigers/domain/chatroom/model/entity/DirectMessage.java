package com.sparta.spartatigers.domain.chatroom.model.entity;

import java.time.LocalDateTime;

import com.sparta.spartatigers.domain.common.entity.BaseEntity;
import com.sparta.spartatigers.domain.user.model.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "direct_message")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DirectMessage extends BaseEntity {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "direct_room_id", nullable = false)
	private DirectRoom directRoom;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sender_id", nullable = false)
	private User sender;

	@Column(nullable = false, length = 500)
	private String message;

	@Column(nullable = false)
	private LocalDateTime sentAt;

	@PrePersist
	protected void onPersist() {
		this.sentAt = LocalDateTime.now();
	}
}
