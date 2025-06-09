package com.sparta.spartatigers.domain.favoriteteam.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.sparta.spartatigers.domain.team.model.entity.Team.Code;
import com.sparta.spartatigers.domain.favoriteteam.model.entity.FavoriteTeam;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AddFavTeamResponseDto {

    private Long userId;
    private Long teamId;
    private String teamName;
    private Code teamCode;

    public static AddFavTeamResponseDto of(FavoriteTeam favoriteTeam) {
        return new AddFavTeamResponseDto(
                favoriteTeam.getUser().getId(),
                favoriteTeam.getTeam().getId(),
                favoriteTeam.getTeam().getName(),
                favoriteTeam.getTeam().getCode());
    }
}
