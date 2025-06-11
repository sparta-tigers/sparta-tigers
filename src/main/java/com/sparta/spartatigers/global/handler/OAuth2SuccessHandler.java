package com.sparta.spartatigers.global.handler;

import java.io.IOException;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import com.sparta.spartatigers.domain.user.model.CustomUserPrincipal;
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

        Map<String, Object> attributes = customUser.getAttributes();
        String providerId = String.valueOf(attributes.get("id"));
        Map<String, Object> kakaoAccount =
                (Map<String, Object>) customUser.getAttributes().get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

        String email = (String) kakaoAccount.get("email");
        String nickname = (String) profile.get("nickname");
        String path = (String) profile.get("profile_image_url");

        log.info("email: {}", email);
        log.info("nickname: {}", nickname);
        log.info("path: {}", path);

        String token = jwtUtil.generateToken(email, "USER");

        if (!userRepository.existsByProviderId(providerId)) {
            User newUser = new User(email, providerId, nickname, path);
            userRepository.save(newUser);
        }

        response.sendRedirect("http://localhost:5173/oauth2/redirect?token=" + token);
    }
}
