package com.sparta.spartatigers.domain.alarm.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import com.sparta.spartatigers.domain.team.model.entity.Team;
import com.sparta.spartatigers.global.exception.ExceptionCode;
import com.sparta.spartatigers.global.exception.ServerException;

@Getter
@AllArgsConstructor
public class TeamNameResponseDto {
    private Long id;
    private String teamName;

    public static TeamNameResponseDto from(Team team) {
        if (team == null) {
            throw new ServerException(ExceptionCode.TEAM_NOT_FOUND);
        }
        return new TeamNameResponseDto(team.getId(), team.getName());
    }
}
