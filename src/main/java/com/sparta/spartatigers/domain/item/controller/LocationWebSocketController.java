package com.sparta.spartatigers.domain.item.controller;

import java.util.Map;

import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;

import com.sparta.spartatigers.domain.item.dto.request.LocationRequestDto;
import com.sparta.spartatigers.domain.item.service.LocationService;

@Controller
@RequiredArgsConstructor
public class LocationWebSocketController {

    private final LocationService locationService;

    @MessageMapping("/location.update")
    public void updateLocation(
            @Payload LocationRequestDto dto,
            @Header("simpSessionAttributes") Map<String, Object> sessionAttributes) {

        Long userId = (Long) sessionAttributes.get("userId");

        locationService.updateLocation(dto, userId);
    }
}
