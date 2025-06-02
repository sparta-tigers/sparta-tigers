package com.sparta.spartatigers.domain.item.service;

import com.sparta.spartatigers.domain.item.dto.request.LocationRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LocationService {

	private static final String USER_LOCATION_KEY = "USERLOCATION:";
	private final RedisTemplate<String, Object> redisTemplate;

	public void createLocation(LocationRequestDto request) {
		// TODO 로그인 기능 들어오면 이 부분 수정
		Long userId = 1L;
		Point point = new Point(request.longitude(), request.latitude());
		redisTemplate.opsForGeo().add(USER_LOCATION_KEY, point, userId);
	}
}
