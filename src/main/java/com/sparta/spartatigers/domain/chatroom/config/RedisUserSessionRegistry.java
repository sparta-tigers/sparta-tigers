package com.sparta.spartatigers.domain.chatroom.config;

import java.time.Duration;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RedisUserSessionRegistry {

    private static final String USER_SESSION_KEY_PREFIX =
            "user-sessions:"; // userId -> Set<sessionId>
    private static final String SESSION_USER_KEY = "session-users"; // sessionId -> userId
    private final StringRedisTemplate redisTemplate;

    // 한 유저가 여러 세션 저장
    public void registerSession(Long userId, String sessionId) {
        String userKey = USER_SESSION_KEY_PREFIX + userId;

        redisTemplate.opsForSet().add(userKey, sessionId);
        redisTemplate.expire(userKey, Duration.ofHours(6)); // 세션 만료 시간 설정

        redisTemplate.opsForHash().put(SESSION_USER_KEY, sessionId, userId.toString());
    }

    public void unregisterSession(Long userId, String sessionId) {
        String userKey = USER_SESSION_KEY_PREFIX + userId;

        redisTemplate.opsForSet().remove(userKey, sessionId);
        redisTemplate.opsForHash().delete(SESSION_USER_KEY, sessionId);

        // 세션이 모두 제거되면 키 자체 제거
        if (Boolean.TRUE.equals(redisTemplate.opsForSet().size(userKey) == 0)) {
            redisTemplate.delete(userKey);
        }
    }

    public boolean isUserConnected(Long userId) {
        String userKey = USER_SESSION_KEY_PREFIX + userId;
        return Boolean.TRUE.equals(redisTemplate.hasKey(userKey));
    }

    public Long getUserIdBySessionId(String sessionId) {
        Object userId = redisTemplate.opsForHash().get(SESSION_USER_KEY, sessionId);
        return userId != null ? Long.parseLong(userId.toString()) : null;
    }

    /*
    나중에 멀티 세션에 대응하려면 꼭 필요한 로직 지금은 사용x
    public Set<String> getSessionIds(Long userId) {
    	String userKey = USER_SESSION_KEY_PREFIX + userId;
    	return redisTemplate.opsForSet().members(userKey);
    }
       */
}
