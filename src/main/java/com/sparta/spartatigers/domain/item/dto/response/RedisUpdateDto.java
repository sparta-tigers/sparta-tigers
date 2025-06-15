package com.sparta.spartatigers.domain.item.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.sparta.spartatigers.domain.item.dto.request.LocationRequestDto;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RedisUpdateDto {

    private Long userId;
    private double latitude;
    private double longitude;

    public static RedisUpdateDto of(Long userId, LocationRequestDto location) {
        return new RedisUpdateDto(userId, location.getLatitude(), location.getLongitude());
    }
}
