package com.sparta.spartatigers.domain.team.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.sparta.spartatigers.domain.common.entity.BaseEntity;

@Entity(name = "players")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Player extends BaseEntity {

    @Column private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teams_id")
    private Team team;

    @Column private String position;
}
