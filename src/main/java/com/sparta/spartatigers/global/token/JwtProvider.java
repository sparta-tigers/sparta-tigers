package com.sparta.spartatigers.global.token;

import java.util.Base64;
import java.util.Date;
import java.util.List;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

import com.sparta.spartatigers.domain.user.model.UserRole;
import com.sparta.spartatigers.global.util.JwtProperties;
import com.sparta.spartatigers.global.util.JwtUtil;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;

@Slf4j
@Component
public class JwtProvider implements TokenProvider {

    private final SecretKey key;
    private final Long jwtExpiration;
    private final JwtUtil jwtUtil;

    public JwtProvider(JwtProperties jwtProperties, JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
        byte[] keyBytes = Base64.getDecoder().decode(jwtProperties.getSecretKey());
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.jwtExpiration = jwtProperties.getExpirationTime();
    }

    @Override
    public String generateAccessToken(Long userId) {
        return generateAccessToken(userId, List.of(UserRole.ROLE_USER));
    }

    @Override
    public String generateAccessToken(Long userId, List<UserRole> roles) {
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + jwtExpiration);

        return Jwts.builder()
                .setSubject(userId.toString())
                .claim(ClaimKey.ROLES.key(), roles)
                .claim(ClaimKey.TYPE.key(), TokenType.ACCESS.name())
                .setIssuedAt(now)
                .setExpiration(expirationDate)
                .signWith(key)
                .compact();
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
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
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
