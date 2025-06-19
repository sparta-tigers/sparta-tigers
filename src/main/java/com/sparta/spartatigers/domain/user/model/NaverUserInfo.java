package com.sparta.spartatigers.domain.user.model;

import java.util.Map;

public class NaverUserInfo implements OAuth2UserInfo {

    private final Map<String, Object> attributes; // 전체 JSON
    private final Map<String, Object> response; // attribute.get

    public NaverUserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
        this.response = (Map<String, Object>) attributes.get("response");
    }

    @Override
    public String getProviderId() {
        return String.valueOf(response.get("id"));
    }

    @Override
    public String getEmail() {
        return (String) response.get("email");
    }

    @Override
    public String getNickname() {
        return (String) response.get("name");
    }

    @Override
    public String getPath() {
        return (String) response.get("profile_image");
    }
}
