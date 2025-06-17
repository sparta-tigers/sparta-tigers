package com.sparta.spartatigers.domain.liveboard.repository;

import java.util.Map;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import com.sparta.spartatigers.domain.liveboard.model.LiveBoardConnection;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class LiveBoardConnectionRepository {

	private final RedisTemplate<String, Object> redisTemplate;
	private static final String PREFIX = "liveboard:connections";

	public void saveConnection(String roomId, String sessionId, LiveBoardConnection connection) {
		redisTemplate.opsForHash().put(PREFIX+roomId, sessionId, connection);
	}

	public void deleteConnection(String roomId, String sessionId) {
		redisTemplate.opsForHash().delete(PREFIX+roomId, sessionId);
	}

	public Long getConnectionCount(String roomId) {
		return redisTemplate.opsForHash().size(PREFIX+roomId);
	}


}
