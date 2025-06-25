package com.sparta.spartatigers.domain.item.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LocationRequestDto {

    private Double latitude;
    private Double longitude;
}
