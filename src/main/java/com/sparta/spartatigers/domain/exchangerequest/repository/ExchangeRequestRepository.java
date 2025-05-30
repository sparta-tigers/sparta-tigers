package com.sparta.spartatigers.domain.exchangerequest.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sparta.spartatigers.domain.exchangerequest.model.entity.ExchangeRequest;
import com.sparta.spartatigers.global.exception.ExceptionCode;
import com.sparta.spartatigers.global.exception.ServerException;

public interface ExchangeRequestRepository extends JpaRepository<ExchangeRequest, Long> {

    default ExchangeRequest findByIdOrElseThrow(Long id) {
        return findById(id)
                .orElseThrow(() -> new ServerException(ExceptionCode.EXCHANGE_REQUEST_NOT_FOUND));
    }
}
