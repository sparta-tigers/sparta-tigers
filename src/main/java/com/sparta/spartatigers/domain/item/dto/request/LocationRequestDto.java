package com.sparta.spartatigers.domain.item.dto.request;

import jakarta.validation.constraints.NotNull;

public record LocationRequestDto(
        @NotNull(message = "위도는 필수입니다.") Double latitude,
        @NotNull(message = "경도는 필수입니다.") Double longitude) {}
