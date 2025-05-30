package com.sparta.spartatigers.domain.exchangerequest.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sparta.spartatigers.domain.exchangerequest.model.entity.ExchangeRequest;

public interface ExchangeRequestRepository extends JpaRepository<ExchangeRequest, Long> {}
