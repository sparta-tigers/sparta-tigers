package com.sparta.spartatigers.global.handler;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import com.sparta.spartatigers.domain.user.model.*;
import com.sparta.spartatigers.domain.user.model.entity.User;
import com.sparta.spartatigers.domain.user.repository.UserRepository;
import com.sparta.spartatigers.global.util.JwtUtil;

@Component
@Log4j2
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        CustomUserPrincipal customUser = (CustomUserPrincipal) authentication.getPrincipal();

        String provider = customUser.getProvider();
        String providerId = customUser.getUser().getProviderId();
        String email = customUser.getUser().getEmail();
        String nickname = customUser.getUser().getNickname();
        String path = customUser.getUser().getPath();

        log.info("email: {}", email);
        log.info("nickname: {}", nickname);
        log.info("path: {}", path);

        String token = jwtUtil.generateToken(email, "USER");

        if (!userRepository.existsByProviderId(providerId)) {
            User newUser = User.from(provider, providerId, email, nickname, path);
            userRepository.save(newUser);
        }

        response.sendRedirect("http://localhost:5173/oauth2/redirect?token=" + token);
    }
}
