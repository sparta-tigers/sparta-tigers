package com.sparta.spartatigers.domain.favoriteteam.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.sparta.spartatigers.domain.favoriteteam.model.entity.FavoriteTeam;
import com.sparta.spartatigers.domain.user.model.entity.User;
import com.sparta.spartatigers.global.exception.ExceptionCode;
import com.sparta.spartatigers.global.exception.InvalidRequestException;

public interface FavoriteTeamRepository extends JpaRepository<FavoriteTeam, Long> {
    boolean existsByUser(User user);

    @Query(
            """
		SELECT f
		FROM favorite_team f
		JOIN FETCH f.user
		JOIN FETCH f.team
		WHERE f.user.id = :userId
		""")
    Optional<FavoriteTeam> findByUserId(Long userId);

    default FavoriteTeam findByUserIdOrElseThrow(Long userId) {
        return findByUserId(userId)
                .orElseThrow(
                        () -> new InvalidRequestException(ExceptionCode.FAVORITE_TEAM_NOT_FOUND));
    }
}
