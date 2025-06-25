package com.sparta.spartatigers.global.util;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisRateLimiter {

	private final StringRedisTemplate redisTemplate;

	/**
	 * redis를 이용해 사용자의 요청 횟수를 제한하는 기능
	 *
	 * @param key       redis에 저장할 키
	 * @param limit     허용 횟수
	 * @param limitTime 제한 시간
	 * @return 요청 횟수가 limit보다 커지면 차단됨
	 */
	public boolean isRateLimited(String key, int limit, Duration limitTime) {
		Long count = redisTemplate.opsForValue().increment(key);

		// redis가 값을 못 가져오는 걸 방어
		if (count == null) {
			return false;
		}

		// 첫 요청이면 limitTime만큼 TTL 설정
		if (count == 1) {
			redisTemplate.expire(key, limitTime);
		}

		// true가 반환되면 rate limit에 걸린 상태라서 차단됨
		return count > limit;
	}
}
