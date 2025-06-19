package com.sparta.spartatigers.domain.user.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.sparta.spartatigers.domain.user.dto.request.LoginRequestDto;
import com.sparta.spartatigers.domain.user.dto.request.SignUpRequestDto;
import com.sparta.spartatigers.domain.user.dto.response.AuthResponseDto;
import com.sparta.spartatigers.domain.user.dto.response.UserInfoResponseDto;
import com.sparta.spartatigers.domain.user.model.CustomUserPrincipal;
import com.sparta.spartatigers.domain.user.model.entity.User;
import com.sparta.spartatigers.domain.user.repository.UserRepository;
import com.sparta.spartatigers.global.exception.ExceptionCode;
import com.sparta.spartatigers.global.exception.ServerException;
import com.sparta.spartatigers.global.util.JwtUtil;

@Service
@RequiredArgsConstructor
public class UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public UserInfoResponseDto getUserInfo(CustomUserPrincipal userPrincipal) {
        return UserInfoResponseDto.from(userPrincipal.getUser());
    }

    @Transactional
    public void createUser(SignUpRequestDto signUpRequestDto) {
        String encodedPassword = passwordEncoder.encode(signUpRequestDto.getPassword());

        if (userRepository.existsByEmail(signUpRequestDto.getEmail())) {
            throw new ServerException(ExceptionCode.EMAIL_ALREADY_USED);
        }

        if (userRepository.existsByNickname(signUpRequestDto.getNickname())) {
            throw new ServerException(ExceptionCode.NICKNAME_ALREADY_USED);
        }

        User user = User.from(signUpRequestDto, encodedPassword);
        userRepository.save(user);
    }

    public AuthResponseDto login(LoginRequestDto loginRequestDto) {
        User user =
                userRepository
                        .findByEmail(loginRequestDto.getEmail())
                        .orElseThrow(() -> new RuntimeException("user not found"));
        if (!passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword())) {
            throw new ServerException(ExceptionCode.PASSWORD_NOT_MATCH);
        }
        String token = jwtUtil.generateToken(loginRequestDto.getEmail(), "ROLE_USER");
        return AuthResponseDto.from(token);
    }
}
