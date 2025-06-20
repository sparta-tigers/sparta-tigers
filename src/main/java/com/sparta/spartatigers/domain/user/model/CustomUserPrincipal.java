package com.sparta.spartatigers.domain.user.model;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import lombok.Getter;

import com.sparta.spartatigers.domain.user.model.entity.User;

@Getter
public class CustomUserPrincipal implements UserDetails, OAuth2User {

    private final User user;
    private final Map<String, Object> attributes;
    private final String provider;

    public CustomUserPrincipal(User user) {
        this.user = user;
        this.attributes = null;
        this.provider = null;
    }

    public CustomUserPrincipal(User user, Map<String, Object> attributes, String provider) {
        this.user = user;
        this.attributes = attributes;
        this.provider = provider;
    }

    public static Long getUserId(CustomUserPrincipal principal) {
        return principal.getUser().getId();
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRoles()));
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return getUser().getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return String.valueOf(user.getId());
    }
}
