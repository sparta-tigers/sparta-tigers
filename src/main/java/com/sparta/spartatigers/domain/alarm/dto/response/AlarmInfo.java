package com.sparta.spartatigers.domain.alarm.dto.response;

import java.time.LocalDateTime;

import lombok.*;

import com.fasterxml.jackson.annotation.JsonFormat;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AlarmInfo {
    private Long userId;
    private Long matchId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime alarmTime;
}
