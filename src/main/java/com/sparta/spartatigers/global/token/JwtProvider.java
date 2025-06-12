package com.sparta.spartatigers.global.token;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import jakarta.annotation.PostConstruct;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.sparta.spartatigers.domain.user.model.UserRole;
import com.sparta.spartatigers.domain.user.service.CustomUserDetailsService;
import com.sparta.spartatigers.global.util.JwtProperties;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtProvider implements TokenProvider {

    private final CustomUserDetailsService userDetailsService;
    private final JwtProperties jwtProperties;

    private Key secretKey;
    private long expirationTime;

    @PostConstruct
    public void init() {
        log.info("JwtProperties.secretKey: {}", jwtProperties.getSecretKey());
        log.info("JwtProperties.expirationTime: {}", jwtProperties.getExpirationTime());

        byte[] decodedKey = Base64.getDecoder().decode(jwtProperties.getSecretKey());
        this.secretKey = Keys.hmacShaKeyFor(decodedKey);
        this.expirationTime = jwtProperties.getExpirationTime();
    }

    @Override
    public String generateAccessToken(Long userId) {
        log.info(
                "generated access token: {}",
                generateAccessToken(userId, List.of(UserRole.ROLE_USER)));
        return generateAccessToken(userId, List.of(UserRole.ROLE_USER));
    }

    @Override
    public String generateAccessToken(Long userId, List<UserRole> roles) {
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + expirationTime);

        return Jwts.builder()
                .setSubject(userId.toString())
                .claim(ClaimKey.ROLES.key(), roles)
                .claim(ClaimKey.TYPE.key(), TokenType.ACCESS.name())
                .setIssuedAt(now)
                .setExpiration(expirationDate)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public Authentication getAuthentication(String token) {
        String username = getUserNameFromToken(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(
                userDetails, "", userDetails.getAuthorities());
    }

    public String getUserNameFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    @Override
    public Date getExpirationFromToken(String token) {
        return getClaimsFromToken(token).getExpiration();
    }

    @Override
    public Date getIssuedAtFromToken(String token) {
        return getClaimsFromToken(token).getIssuedAt();
    }

    @Override
    public Long getUserIdFromToken(String token) {
        String userIdString = getClaimsFromToken(token).getSubject();
        return Long.parseLong(userIdString);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<UserRole> getRolesFromToken(String token) {
        return (List<UserRole>) getClaimsFromToken(token).get(ClaimKey.ROLES.key(), List.class);
    }

    @Override
    public boolean validateToken(String token) {
        log.debug("JWT 입력값: {}", token); // 앞뒤 공백 확인

        try {
            getClaimsFromToken(token);
            return true;
        } catch (MalformedJwtException e) {
            log.warn("Invalid Jwt Token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.warn("Expired Jwt Token: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.warn("Unsupported Jwt Token: {}", e.getMessage());
        } catch (Exception e) {
            log.error("UnExpected Exception When Token Validating: {}", e.getMessage());
        }

        return false;
    }

    private Claims getClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private enum ClaimKey {
        USER_ID("userId"),
        TYPE("type"),
        ROLES("roles");

        private final String key;

        ClaimKey(String key) {
            this.key = key;
        }

        public String key() {
            return key;
        }
    }

    private enum TokenType {
        ACCESS,
        REFRESH
    }
}
