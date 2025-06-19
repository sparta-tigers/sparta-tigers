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
import com.sparta.spartatigers.domain.user.dto.request.SignUpRequestDto;

@Entity(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class User extends BaseEntity implements Serializable {

    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column private String provider;

    @Column private String providerId;

    @Column private String email;

    @Column private String password;

    @Column private String nickname;

    @Column private String path;

    @Enumerated(EnumType.STRING)
    private Role roles;

    @Column private LocalDateTime deletedAt;

    // 소셜 로그인
    public User(String email, String providerId, String nickname, String path) {
        this.email = email;
        this.providerId = providerId;
        this.nickname = nickname;
        this.path = path;
        this.roles = Role.USER;
    }

    // 일반 로그인
    public User(String email, String provider, String nickname, String path, String password) {
        this.email = email;
        this.providerId = provider;
        this.nickname = nickname;
        this.path = path;
        this.password = password;
        this.roles = Role.USER;
    }

    public static User from(SignUpRequestDto signUpRequestDto, String encodedPassword) {
        return new User(
                signUpRequestDto.getEmail(),
                "local",
                signUpRequestDto.getNickname(),
                null,
                encodedPassword);
    }

    public void deleted() {
        this.deletedAt = LocalDateTime.now();
    }

    public enum Role {
        ADMIN,
        USER
    }
}
