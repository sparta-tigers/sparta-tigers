package com.sparta.spartatigers.domain.item.pubsub;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import com.sparta.spartatigers.domain.item.dto.response.RedisUpdateDto;
import com.sparta.spartatigers.domain.item.service.LocationService;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
public class LocationSubscriber implements MessageListener {

    private static final double NEARBY_RADIUS_KM = 0.05;
    private final ObjectMapper objectMapper;
    private final SimpMessagingTemplate messagingTemplate;
    private final LocationService locationService;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        RedisUpdateDto dto = deserialize(message);
        Long userId = dto.getUserId();

        List<Long> nearByUserIds = locationService.findUsersNearBy(userId, NEARBY_RADIUS_KM);
        nearByUserIds.forEach(
                targetUserId -> {
                    String destination = "/server/items/user/" + targetUserId;
                    messagingTemplate.convertAndSend(
                            destination, Map.of("type", "USER_LOCATION_UPDATE", "data", dto));
                });

        String myDestination = "/server/items/user/" + userId;
        messagingTemplate.convertAndSend(myDestination, Map.of("type", "REFRESH_ITEMS"));
    }

    private RedisUpdateDto deserialize(Message message) {
        try {
            String json = new String(message.getBody(), StandardCharsets.UTF_8);
            return objectMapper.readValue(json, RedisUpdateDto.class);
        } catch (JsonProcessingException e) {
            System.err.println("Redis 메시지 역직렬화 실패: " + e.getMessage());
            throw new RuntimeException("Redis 메시지 역직렬화 실패", e);
        }
    }
}
