package com.sparta.spartatigers.domain.user.model;

public interface OAuth2UserInfo {
    String getProviderId();

    String getEmail();

    String getNickname();

    String getPath();
}
