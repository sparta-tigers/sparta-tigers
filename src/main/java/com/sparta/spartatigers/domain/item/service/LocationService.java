package com.sparta.spartatigers.domain.item.service;

import org.springframework.data.geo.Point;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import com.sparta.spartatigers.domain.item.dto.request.LocationRequestDto;
import com.sparta.spartatigers.domain.user.model.CustomUserPrincipal;

@Service
@RequiredArgsConstructor
public class LocationService {

    private static final String USER_LOCATION_KEY = "USERLOCATION:";
    private final RedisTemplate<String, Object> redisTemplate;

    public void createLocation(LocationRequestDto request, CustomUserPrincipal userPrincipal) {

        Long userId = userPrincipal.getUser().getId();
        Point point = new Point(request.longitude(), request.latitude());
        redisTemplate.opsForGeo().add(USER_LOCATION_KEY, point, userId);
    }
}
