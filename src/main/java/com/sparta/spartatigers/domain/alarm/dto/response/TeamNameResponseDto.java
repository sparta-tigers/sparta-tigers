package com.sparta.spartatigers.domain.alarm.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import com.sparta.spartatigers.domain.team.model.entity.Team;

@Getter
@AllArgsConstructor
public class TeamNameResponseDto {
    private Long id;
    private String teamName;

    public static TeamNameResponseDto from(Team team) {
        return new TeamNameResponseDto(team.getId(), team.getName());
    }
}
