package com.sparta.spartatigers.domain.alarm.dto.request;

import java.time.temporal.ChronoUnit;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.sparta.spartatigers.domain.alarm.model.entity.Alarm;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AlarmUpdateDto {
    @NotNull private Long id;
    private Integer minutes;
    private Integer preMinutes;

    @AssertTrue(message = "minutes 또는 preMinutes 중 하나는 필수입니다.")
    public boolean isAtLeastOneSet() {
        return minutes != null || preMinutes != null;
    }

    public static AlarmUpdateDto from(Alarm alarm) {
        AlarmUpdateDto dto = new AlarmUpdateDto();
        dto.id = alarm.getId();

        if (alarm.getNormalAlarmTime() != null && alarm.getMatch() != null) {
            long diffMinutes =
                    ChronoUnit.MINUTES.between(
                            alarm.getMatch().getMatchTime(), alarm.getNormalAlarmTime());
            dto.minutes = (int) -diffMinutes; // minus이니까 부호 반전
        }

        if (alarm.getPreAlarmTime() != null && alarm.getMatch() != null) {
            long diffPreMinutes =
                    ChronoUnit.MINUTES.between(
                            alarm.getMatch().getMatchTime(), alarm.getPreAlarmTime());
            dto.preMinutes = (int) -diffPreMinutes;
        }

        return dto;
    }
}
