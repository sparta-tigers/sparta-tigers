package com.sparta.spartatigers.domain.item.model.entity;

import com.sparta.spartatigers.domain.common.entity.BaseEntity;
import com.sparta.spartatigers.domain.user.model.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "items")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Item extends BaseEntity {

	@Enumerated(EnumType.STRING)
	private Category category;

	@Column
	private String image;

	@Column
	private String seatInfo;

	@Column
	private String title;

	@Column
	private String description;

	@Enumerated(EnumType.STRING)
	private Status status;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	public enum Category {
		GOODS, TICKET
	}

	public enum Status {
		REGISTERED, COMPLETED
	}
}
