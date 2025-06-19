package com.sparta.spartatigers.domain.chatroom.registry;

import java.time.Duration;
import java.util.Set;

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

    // 멀티 세션 불가 x (메시지 중복 수신 문제 발생)
    // TODO: 추후에 멀티 디바이스 환경에도 사용할 수 있게 하려면 멀티 세션이 가능하게 대응
    public void registerSession(Long userId, String sessionId) {
        String userKey = USER_SESSION_KEY_PREFIX + userId;

        // 기존 세션 제거
        Set<String> existingSessions = getSessionIds(userId);
        if (existingSessions != null) {
            for (String oldSessionId : existingSessions) {
                redisTemplate.opsForSet().remove(userKey, oldSessionId);
                redisTemplate.opsForHash().delete(SESSION_USER_KEY, oldSessionId);
            }
        }

        // 새 세션 등록
        // userId별로 세션ID를 Set에 추가
        redisTemplate.opsForSet().add(userKey, sessionId);
        redisTemplate.expire(userKey, Duration.ofHours(6)); // 세션 만료 시간 설정
        // 세션ID에 userId 해시에 등록
        redisTemplate.opsForHash().put(SESSION_USER_KEY, sessionId, userId.toString());
    }

    public void unregisterSession(Long userId, String sessionId) {
        String userKey = USER_SESSION_KEY_PREFIX + userId;

        // Set에서 세션ID 제거
        redisTemplate.opsForSet().remove(userKey, sessionId);
        // 해시에서 세션ID 제거
        redisTemplate.opsForHash().delete(SESSION_USER_KEY, sessionId);

        // 세션이 모두 제거되면 키 자체 제거
        if (Boolean.TRUE.equals(redisTemplate.opsForSet().size(userKey) == 0)) {
            redisTemplate.delete(userKey);
        }
    }

    public boolean isUserConnected(Long userId) {
        String userKey = USER_SESSION_KEY_PREFIX + userId;
        // userKey가 Redis에 존재하는지 확인
        return Boolean.TRUE.equals(redisTemplate.hasKey(userKey));
    }

    public Long getUserIdBySessionId(String sessionId) {
        Object userId = redisTemplate.opsForHash().get(SESSION_USER_KEY, sessionId);
        return userId != null ? Long.parseLong(userId.toString()) : null;
    }

    public Set<String> getSessionIds(Long userId) {
        String userKey = USER_SESSION_KEY_PREFIX + userId;
        return redisTemplate.opsForSet().members(userKey);
    }
}
