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

    // 한 유저가 여러 세션 저장
    public void registerSession(Long userId, String sessionId) {
        String userKey = USER_SESSION_KEY_PREFIX + userId;

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

    // TODO: 나중에 멀티 세션에 대응하려면 꼭 필요한 로직 지금은 사용x
    // TODO: 추후에 한 사용자가 여러 브라우저로 로그인했을 때 한 명의 유저에게 여러 세션이 생기는 멀티 세션에 대응하기 위함
    public Set<String> getSessionIds(Long userId) {
        String userKey = USER_SESSION_KEY_PREFIX + userId;
        return redisTemplate.opsForSet().members(userKey);
    }
}
