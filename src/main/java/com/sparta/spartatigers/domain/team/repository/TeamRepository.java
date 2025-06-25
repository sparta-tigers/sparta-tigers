package com.sparta.spartatigers.domain.team.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sparta.spartatigers.domain.team.model.entity.Team;
import com.sparta.spartatigers.global.exception.ExceptionCode;
import com.sparta.spartatigers.global.exception.InvalidRequestException;

public interface TeamRepository extends JpaRepository<Team, Long> {

    default Team findByIdOrElseThrow(Long teamId) {
        return findById(teamId)
                .orElseThrow(() -> new InvalidRequestException(ExceptionCode.TEAM_NOT_FOUND));
    }
}
