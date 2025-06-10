package com.sparta.spartatigers.domain.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sparta.spartatigers.domain.user.model.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    boolean existsByProviderId(String providerId);

    Optional<User> findByProviderId(String providerId);
}
