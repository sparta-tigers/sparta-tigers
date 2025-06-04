package com.sparta.spartatigers.domain.user.model.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;

import lombok.*;

import com.sparta.spartatigers.domain.common.entity.BaseEntity;

@Entity(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class User extends BaseEntity {

    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column private String email;

    @Column private String nickname;

    @Column private String path;

    @Enumerated(EnumType.STRING)
    private Role roles;

    @Column private LocalDateTime deletedAt;

    public User(String email, String nickname, String path) {
        this.email = email;
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
