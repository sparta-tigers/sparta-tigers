package com.sparta.spartatigers.domain.alarm.dto.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;

import lombok.Getter;

@Getter
public class AlarmUpdateDto {
    @NotNull private Long id;
    private Integer minutes;
    private Integer preMinutes;

    @AssertTrue(message = "minutes 또는 preMinutes 중 하나는 필수입니다.")
    public boolean isAtLeastOneSet() {
        return minutes != null || preMinutes != null;
    }
}
