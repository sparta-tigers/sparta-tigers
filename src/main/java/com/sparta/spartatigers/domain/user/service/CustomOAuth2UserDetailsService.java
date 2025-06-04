package com.sparta.spartatigers.domain.user.service;

import java.util.Map;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.sparta.spartatigers.domain.user.model.entity.User;
import com.sparta.spartatigers.domain.user.repository.UserRepository;

@RequiredArgsConstructor
@Service
@Slf4j
public class CustomOAuth2UserDetailsService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        String accessToken = userRequest.getAccessToken().getTokenValue();
        System.out.println("Access Token = " + accessToken);
        OAuth2User oAuth2User = new DefaultOAuth2UserService().loadUser(userRequest);
        Map<String, Object> kakaoAccount =
                (Map<String, Object>) oAuth2User.getAttributes().get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

        String email = (String) kakaoAccount.get("email");
        String nickname = (String) profile.get("nickname");
        String path = (String) profile.get("profile_image_url");

        log.info("email{}", email);
        log.info("nickname{}", nickname);
        log.info("profileImage{}", path);

        // 사용자 정보 DB에 저장 or 조회
        User user =
                userRepository
                        .findByEmail(email)
                        .orElseGet(
                                () -> {
                                    User newUser = new User(email, nickname, path);
                                    return userRepository.save(newUser);
                                });
        return oAuth2User;
    }
}
