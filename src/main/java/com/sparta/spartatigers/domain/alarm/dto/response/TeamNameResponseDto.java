package com.sparta.spartatigers.domain.alarm.dto.response;

import jakarta.validation.constraints.NotBlank;

import lombok.Getter;

@Getter
public class TeamNameResponseDto {
    @NotBlank private String teamName;
}
