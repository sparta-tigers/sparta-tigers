package com.sparta.spartatigers.domain.team.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sparta.spartatigers.domain.team.model.entity.Stadium;

public interface StadiumRepository extends JpaRepository<Stadium, Long> {}
