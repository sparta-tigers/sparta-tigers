package com.sparta.spartatigers.domain.team.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.sparta.spartatigers.domain.common.entity.BaseEntity;

@Entity(name = "teams")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Team extends BaseEntity {

    @Column private String name;

    @Column
    @Enumerated(value = EnumType.STRING)
    private Code code;

    public enum Code {
        LG,
        KT,
        OB,
        HT,
        SS,
        LT,
        NC,
        SK,
        HH,
        WO
    }
}
