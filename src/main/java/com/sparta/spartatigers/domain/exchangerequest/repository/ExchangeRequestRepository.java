package com.sparta.spartatigers.domain.exchangerequest.repository;

import com.sparta.spartatigers.domain.exchangerequest.model.entity.ExchangeRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExchangeRequestRepository extends JpaRepository<ExchangeRequest, Long> {

}
