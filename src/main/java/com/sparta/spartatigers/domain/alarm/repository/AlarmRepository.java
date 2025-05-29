package com.sparta.spartatigers.domain.alarm.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sparta.spartatigers.domain.alarm.model.entity.Alarm;

public interface AlarmRepository extends JpaRepository<Alarm, Long> {

}
