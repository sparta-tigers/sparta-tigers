package com.sparta.spartatigers.domain.item.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
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
    private static final double SEARCH_RADIUS_KM = 0.05;
    private final RedisTemplate<String, Object> redisTemplate;
    private final LocationPublisher locationPublisher;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public void updateLocation(LocationRequestDto request, Long userId) {

        if (userId == null) {
            return;
        }

        Point point = new Point(request.getLongitude(), request.getLatitude());
        redisTemplate.opsForGeo().add(USER_LOCATION_KEY, point, userId);

        locationPublisher.publishLocation(RedisUpdateDto.of(userId, request));
    }

    @Transactional(readOnly = true)
    public List<Long> findUsersNearBy(Long userId, double radius) {

        Point userPoint = redisTemplate.opsForGeo().position(USER_LOCATION_KEY, userId).get(0);

        if (userPoint == null) {
            return List.of();
        }
        Distance distance = new Distance(radius, Metrics.KILOMETERS);
        Circle circle = new Circle(userPoint, distance);
        GeoResults<RedisGeoCommands.GeoLocation<Object>> results =
                redisTemplate.opsForGeo().radius(USER_LOCATION_KEY, circle);

        if (results == null) {
            return List.of();
        }
        return results.getContent().stream()
                .map(result -> Long.valueOf(result.getContent().getName().toString()))
                .filter(id -> !id.equals(userId))
                .collect(Collectors.toList());
    }

    public void notifyUsersNearBy(Long userId, String messageType, Object data) {

        List<Long> nearByUserIds = findUsersNearBy(userId, SEARCH_RADIUS_KM);
        nearByUserIds.add(userId);

        Map<String, Object> messagePayload = Map.of("type", messageType, "data", data);

        nearByUserIds.forEach(
                targetUserId -> {
                    String destination = "/server/items/user/" + targetUserId;
                    messagingTemplate.convertAndSend(destination, messagePayload);
                });
    }
}
