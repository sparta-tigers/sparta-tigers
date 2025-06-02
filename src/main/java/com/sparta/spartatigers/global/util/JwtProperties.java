package com.sparta.spartatigers.global.util;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@ConfigurationProperties(prefix = "jwt")
@Getter
@Setter // 없으면 시간 주입 불가
public class JwtProperties {
    private String secretKey;
    private long expirationTime;
}
