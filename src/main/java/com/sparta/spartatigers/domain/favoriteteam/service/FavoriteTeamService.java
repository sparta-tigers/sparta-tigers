package com.sparta.spartatigers.domain.favoriteteam.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.sparta.spartatigers.domain.favoriteteam.dto.request.FavTeamRequestDto;
import com.sparta.spartatigers.domain.favoriteteam.dto.response.FavTeamResponseDto;
import com.sparta.spartatigers.domain.favoriteteam.model.entity.FavoriteTeam;
import com.sparta.spartatigers.domain.favoriteteam.repository.FavoriteTeamRepository;
import com.sparta.spartatigers.domain.team.model.entity.Team;
import com.sparta.spartatigers.domain.team.repository.TeamRepository;
import com.sparta.spartatigers.domain.user.model.entity.User;
import com.sparta.spartatigers.domain.user.repository.UserRepository;
import com.sparta.spartatigers.global.exception.ExceptionCode;
import com.sparta.spartatigers.global.exception.InvalidRequestException;
import com.sparta.spartatigers.global.exception.ServerException;

@Service
@RequiredArgsConstructor
public class FavoriteTeamService {
    private final FavoriteTeamRepository favTeamRepository;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;

    @Transactional
    public FavTeamResponseDto add(FavTeamRequestDto request, Long userId) {
        User user = userRepository.findByIdOrElseThrow(userId);
        // 한 명의 유저는 한 팀만 응원하는 팀에 등록할 수 있음
        boolean exists = favTeamRepository.existsByUser(user);
        if (exists) {
            throw new InvalidRequestException(ExceptionCode.ALREADY_EXISTS_FAVORITE_TEAM);
        }
        Team team = teamRepository.findByIdOrElseThrow(request.getTeamId());

        FavoriteTeam favoriteTeam = FavoriteTeam.from(user, team);
        favTeamRepository.save(favoriteTeam);

        return FavTeamResponseDto.of(favoriteTeam);
    }

    @Transactional(readOnly = true)
    public FavTeamResponseDto get(Long userId) {
        userRepository
                .findById(userId)
                .orElseThrow(() -> new ServerException(ExceptionCode.USER_NOT_FOUND));
        FavoriteTeam findFavoriteTeam = favTeamRepository.findByUserIdOrElseThrow(userId);

        return FavTeamResponseDto.of(findFavoriteTeam);
    }

    @Transactional
    public FavTeamResponseDto update(FavTeamRequestDto request, Long userId) {
        userRepository
                .findById(userId)
                .orElseThrow(() -> new ServerException(ExceptionCode.USER_NOT_FOUND));
        FavoriteTeam findFavoriteTeam = favTeamRepository.findByUserIdOrElseThrow(userId);

        Team newTeam = teamRepository.findByIdOrElseThrow(request.getTeamId());

        findFavoriteTeam.update(newTeam);
        return FavTeamResponseDto.of(findFavoriteTeam);
    }

    @Transactional
    public Void delete(Long userId) {
        userRepository
                .findById(userId)
                .orElseThrow(() -> new ServerException(ExceptionCode.USER_NOT_FOUND));
        FavoriteTeam findFavoriteTeam = favTeamRepository.findByUserIdOrElseThrow(userId);

        favTeamRepository.delete(findFavoriteTeam);

        return null;
    }
}
