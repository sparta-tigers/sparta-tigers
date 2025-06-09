package com.sparta.spartatigers.domain.favoriteteam.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.sparta.spartatigers.domain.favoriteteam.model.entity.FavoriteTeam;
import com.sparta.spartatigers.domain.team.model.entity.Team;
import com.sparta.spartatigers.domain.user.model.entity.User;
import com.sparta.spartatigers.global.exception.ExceptionCode;
import com.sparta.spartatigers.global.exception.InvalidRequestException;

public interface FavoriteTeamRepository extends JpaRepository<FavoriteTeam, Long> {
    boolean existsByUserAndTeam(User user, Team team);

    @Query(
            """
		SELECT f
		FROM favorite_team f
		JOIN FETCH f.user
		JOIN FETCH f.team
		WHERE f.user.id = :userId
		""")
    List<FavoriteTeam> findByUserId(Long userId);

    default List<FavoriteTeam> findByUserIdOrElseThrow(Long userId) {
        List<FavoriteTeam> favoriteTeams = findByUserId(userId);
        if (favoriteTeams == null || favoriteTeams.isEmpty()) {
            throw new InvalidRequestException(ExceptionCode.FAVORITE_TEAM_NOT_FOUND);
        }
        return favoriteTeams;
    }
}
