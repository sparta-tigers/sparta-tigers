package com.sparta.spartatigers.domain.alarm.model.entity;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import lombok.*;

import com.sparta.spartatigers.domain.match.model.entity.Match;
import com.sparta.spartatigers.domain.user.model.entity.User;

@Entity(name = "alarms")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Alarm {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column private LocalDateTime normalAlarmTime;

    @Column private LocalDateTime preAlarmTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id", nullable = false)
    private Match match;

    public void updateAlarmTimes(Integer minutes, Integer preMinutes, LocalDateTime matchTime) {
        if (minutes != null) {
            this.normalAlarmTime = matchTime.minusMinutes(minutes).truncatedTo(ChronoUnit.MINUTES);
        }
        if (preMinutes != null) {
            this.preAlarmTime = matchTime.minusMinutes(preMinutes).truncatedTo(ChronoUnit.MINUTES);
        }
    }

    public static Alarm of(User user, Match match, Integer minutes, Integer preMinutes) {
        LocalDateTime matchStartTime = match.getMatchTime();
        LocalDateTime normalAlarmTime = null;
        LocalDateTime preAlarmTime = null;

        if (minutes != null) {
            normalAlarmTime = matchStartTime.minusMinutes(minutes);
        }

        if (preMinutes != null) {
            preAlarmTime = matchStartTime.minusMinutes(preMinutes);
        }

        Alarm alarm = new Alarm();
        alarm.user = user;
        alarm.match = match;
        alarm.normalAlarmTime = normalAlarmTime;
        alarm.preAlarmTime = preAlarmTime;
        return alarm;
    }

    public void updateNormalAlarmTime(LocalDateTime normalAlarmTime) {
        this.normalAlarmTime = normalAlarmTime;
    }

    public void updatePreAlarmTime(LocalDateTime preAlarmTime) {
        this.preAlarmTime = preAlarmTime;
    }
}
