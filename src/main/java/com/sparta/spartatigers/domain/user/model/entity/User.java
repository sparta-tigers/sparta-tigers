package com.sparta.spartatigers.domain.user.model.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.*;

import org.hibernate.annotations.SQLRestriction;

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
@SQLRestriction("deleted_at is null")
public class User extends BaseEntity implements Serializable {

    @Id
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
    public User(String email, String provider, String providerId, String nickname, String path) {
        this.email = email;
        this.provider = provider;
        this.providerId = providerId;
        this.nickname = nickname;
        this.path = path;
        this.roles = Role.USER;
    }

    // 일반 로그인
    public User(String email, String nickname, String password) {
        this.email = email;
        this.provider = "local";
        this.providerId = null;
        this.nickname = nickname;
        this.password = password;
        this.path = null;
        this.roles = Role.USER;
    }

    public static User from(SignUpRequestDto signUpRequestDto, String encodedPassword) {
        return new User(
                signUpRequestDto.getEmail(), signUpRequestDto.getNickname(), encodedPassword);
    }

    public static User from(
            String provider, String providerId, String email, String nickname, String path) {
        return new User(email, provider, providerId, nickname, path);
    }

    // 이미지 수정
    public void updatePath(String filePath) {
        this.path = filePath;
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }

    public void deleted() {
        this.deletedAt = LocalDateTime.now();
    }

    public enum Role {
        ADMIN,
        USER
    }
}
