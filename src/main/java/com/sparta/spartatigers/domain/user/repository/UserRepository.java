package com.sparta.spartatigers.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sparta.spartatigers.domain.user.model.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
