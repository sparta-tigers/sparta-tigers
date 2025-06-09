package com.sparta.spartatigers.domain.favoriteteam.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sparta.spartatigers.domain.team.model.entity.Team;
import com.sparta.spartatigers.domain.favoriteteam.model.entity.FavoriteTeam;
import com.sparta.spartatigers.domain.user.model.entity.User;

public interface FavoriteTeamRepository extends JpaRepository<FavoriteTeam, Long> {
    boolean existsByUserAndTeam(User user, Team team);
}
