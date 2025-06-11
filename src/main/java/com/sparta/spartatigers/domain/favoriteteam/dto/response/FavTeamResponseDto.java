package com.sparta.spartatigers.domain.favoriteteam.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.sparta.spartatigers.domain.favoriteteam.model.entity.FavoriteTeam;
import com.sparta.spartatigers.domain.team.model.entity.Team.Code;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FavTeamResponseDto {

    private Long userId;
    private Long teamId;
    private String teamName;
    private Code teamCode;

    public static FavTeamResponseDto of(FavoriteTeam favoriteTeam) {
        return new FavTeamResponseDto(
                favoriteTeam.getUser().getId(),
                favoriteTeam.getTeam().getId(),
                favoriteTeam.getTeam().getName(),
                favoriteTeam.getTeam().getCode());
    }
}
