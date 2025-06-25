package com.sparta.spartatigers.domain.alarm.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.sparta.spartatigers.domain.alarm.model.entity.Alarm;

public interface AlarmRepository extends JpaRepository<Alarm, Long> {
    @Query(
            "select a from alarms a join fetch a.match m join fetch m.homeTeam join fetch m.awayTeam join fetch m.stadium where a.user.id = :id")
    List<Alarm> findAllByUserIdWithMatchAndTeams(Long id);

    Optional<Alarm> findByUser_IdAndMatch_Id(Long userId, Long matchId);
}
