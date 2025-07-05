package com.sparta.spartatigers.domain.alarm.dto.request;

import jakarta.validation.constraints.*;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AlarmRegisterDto {
    @NotNull private Long id;

    @Min(1)
    @Max(300)
    private Integer minutes;

    @Min(1)
    @Max(300)
    private Integer preMinutes;

    @AssertTrue(message = "minutes 또는 preMinutes 중 하나는 필수입니다.")
    public boolean isAtLeastOneSet() {
        return minutes != null || preMinutes != null;
    }
}
