package com.sparta.spartatigers.domain.user.model.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.sparta.spartatigers.domain.common.entity.BaseEntity;

@Entity(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class User extends BaseEntity implements Serializable {

    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column private String providerId;

    @Column private String email;

    @Column private String nickname;

    @Column private String path;

    @Enumerated(EnumType.STRING)
    private Role roles;

    @Column private LocalDateTime deletedAt;

    public User(String email, String providerId, String nickname, String path) {
        this.email = email;
        this.providerId = providerId;
        this.nickname = nickname;
        this.path = path;
        this.roles = Role.USER;
    }

    public void deleted() {
        this.deletedAt = LocalDateTime.now();
    }

    public enum Role {
        ADMIN,
        USER
    }
}
