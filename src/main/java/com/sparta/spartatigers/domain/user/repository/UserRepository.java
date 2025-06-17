package com.sparta.spartatigers.domain.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.sparta.spartatigers.domain.user.model.entity.User;
import com.sparta.spartatigers.global.exception.ExceptionCode;
import com.sparta.spartatigers.global.exception.InvalidRequestException;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    boolean existsByProviderId(String providerId);

    Optional<User> findByProviderId(String providerId);

    boolean existsByEmail(String email);

    default User findByIdOrElseThrow(Long userId) {
        return findById(userId)
                .orElseThrow(() -> new InvalidRequestException(ExceptionCode.USER_NOT_FOUND));
    }

    @Query("SELECT u.nickname FROM users u WHERE u.id = :userId")
    Optional<String> findNicknameById(Long userId);
}
