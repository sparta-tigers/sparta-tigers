package com.sparta.spartatigers.domain.item.service;

import org.springframework.data.geo.Point;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.sparta.spartatigers.domain.item.dto.request.LocationRequestDto;
import com.sparta.spartatigers.domain.item.dto.response.RedisUpdateDto;
import com.sparta.spartatigers.domain.item.pubsub.LocationPublisher;

@Service
@RequiredArgsConstructor
public class LocationService {

    private static final String USER_LOCATION_KEY = "USERLOCATION:";
    private final RedisTemplate<String, Object> redisTemplate;
    private final LocationPublisher locationPublisher;

    @Transactional
    public void updateLocation(LocationRequestDto request, Long userId) {

        Point point = new Point(request.getLongitude(), request.getLatitude());
        redisTemplate.opsForGeo().add(USER_LOCATION_KEY, point, userId);

        locationPublisher.publishLocation(RedisUpdateDto.of(userId, request));
    }
}
