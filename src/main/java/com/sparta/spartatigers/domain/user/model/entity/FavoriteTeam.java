package com.sparta.spartatigers.domain.user.model.entity;

import com.sparta.spartatigers.domain.common.entity.BaseEntity;
import com.sparta.spartatigers.domain.team.model.entity.Team;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "favorite_team")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteTeam extends BaseEntity {

	@JoinColumn(name = "user_id")
	@ManyToOne(fetch = FetchType.LAZY)
	private User user;

	@JoinColumn(name = "team_id")
	@ManyToOne(fetch = FetchType.LAZY)
	private Team team;
}
