package com.sparta.spartatigers.domain.team.model.entity;

import com.sparta.spartatigers.domain.common.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "stadiums")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Stadium extends BaseEntity {

	@Column
	private String name;
}
