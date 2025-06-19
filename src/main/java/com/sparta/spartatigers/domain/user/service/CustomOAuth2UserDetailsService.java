package com.sparta.spartatigers.domain.user.service;

import java.util.Map;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.sparta.spartatigers.domain.user.model.*;
import com.sparta.spartatigers.domain.user.model.entity.User;
import com.sparta.spartatigers.domain.user.repository.UserRepository;
import com.sparta.spartatigers.global.exception.ExceptionCode;
import com.sparta.spartatigers.global.exception.ServerException;

@RequiredArgsConstructor
@Service
@Slf4j
public class CustomOAuth2UserDetailsService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, Object> attributes = oAuth2User.getAttributes();
        String registrationId =
                userRequest.getClientRegistration().getRegistrationId(); // google, kakao, naver

        OAuth2UserInfo userInfo =
                switch (registrationId) {
                    case "kakao" -> new KakaoUserInfo(attributes);
                    case "google" -> new GoogleUserInfo(attributes);
                    case "naver" -> new NaverUserInfo(attributes);
                    default -> throw new ServerException(ExceptionCode.NOT_SUPPORTED_SOCIAL_LOGIN);
                };

        String providerId = userInfo.getProviderId();
        String email = userInfo.getEmail();
        String nickname = userInfo.getNickname();
        String path = userInfo.getPath();

        User user =
                userRepository
                        .findByProviderId(providerId)
                        .orElseGet(
                                () -> {
                                    User newUser =
                                            User.from(
                                                    registrationId,
                                                    providerId,
                                                    email,
                                                    nickname,
                                                    path);
                                    return userRepository.save(newUser);
                                });
        return new CustomUserPrincipal(user, attributes, registrationId);
    }
}
