package com.sparta.spartatigers.domain.favoriteteam.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.sparta.spartatigers.domain.favoriteteam.dto.request.AddFavTeamRequestDto;
import com.sparta.spartatigers.domain.favoriteteam.dto.response.AddFavTeamResponseDto;
import com.sparta.spartatigers.domain.favoriteteam.repository.FavoriteTeamRepository;
import com.sparta.spartatigers.domain.team.model.entity.Team;
import com.sparta.spartatigers.domain.team.repository.TeamRepository;
import com.sparta.spartatigers.domain.user.model.CustomUserPrincipal;
import com.sparta.spartatigers.domain.favoriteteam.model.entity.FavoriteTeam;
import com.sparta.spartatigers.domain.user.model.entity.User;
import com.sparta.spartatigers.global.exception.ExceptionCode;
import com.sparta.spartatigers.global.exception.InvalidRequestException;

@Service
@RequiredArgsConstructor
public class FavoriteTeamService {
    private final FavoriteTeamRepository favTeamRepository;
    private final TeamRepository teamRepository;

    @Transactional
    public AddFavTeamResponseDto addFavoriteTeam(
            AddFavTeamRequestDto request, CustomUserPrincipal principal) {
        User user = principal.getUser();
        Team team =
                teamRepository
                        .findById(request.getTeamId())
                        .orElseThrow(
                                () -> new InvalidRequestException(ExceptionCode.TEAM_NOT_FOUND));

        boolean exists = favTeamRepository.existsByUserAndTeam(user, team);
        if (exists) {
            throw new InvalidRequestException(ExceptionCode.DUPLICATE_FAVORITE_TEAM);
        }

        FavoriteTeam favoriteTeam = FavoriteTeam.from(user, team);
        favTeamRepository.save(favoriteTeam);

        return AddFavTeamResponseDto.of(favoriteTeam);
    }
}
