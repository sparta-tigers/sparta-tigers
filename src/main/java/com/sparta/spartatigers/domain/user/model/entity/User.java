package com.sparta.spartatigers.domain.user.model.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.sparta.spartatigers.domain.common.entity.BaseEntity;

@Entity(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class User extends BaseEntity {
    @Column private String providerId;

    @Column private String provider;

    @Column private String email;

    @Column private String nickname;

    @Column private String path;

    @Enumerated(EnumType.STRING)
    private Role roles;

    @Column private LocalDateTime deletedAt;

    public void deleted() {
        this.deletedAt = LocalDateTime.now();
    }

    public enum Role {
        ADMIN,
        USER
    }
}
