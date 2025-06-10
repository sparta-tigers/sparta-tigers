package com.sparta.spartatigers.domain.user.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import com.sparta.spartatigers.domain.user.dto.UserInfoResponseDto;
import com.sparta.spartatigers.domain.user.model.CustomUserPrincipal;
import com.sparta.spartatigers.domain.user.model.entity.User;

@Service
@RequiredArgsConstructor
public class UserService {

    public UserInfoResponseDto getUserInfo(CustomUserPrincipal userPrincipal) {
        User user = userPrincipal.getUser();
        return new UserInfoResponseDto(
                user.getId(),
                user.getEmail(),
                user.getProviderId(),
                user.getNickname(),
                user.getPath(),
                user.getRoles().name());
    }
}
