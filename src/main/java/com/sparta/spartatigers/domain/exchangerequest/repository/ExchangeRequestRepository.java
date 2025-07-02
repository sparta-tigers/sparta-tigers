package com.sparta.spartatigers.domain.exchangerequest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sparta.spartatigers.domain.exchangerequest.model.entity.ExchangeRequest;
import com.sparta.spartatigers.global.exception.ExceptionCode;
import com.sparta.spartatigers.global.exception.ServerException;

public interface ExchangeRequestRepository
        extends JpaRepository<ExchangeRequest, Long>, ExchangeRequestQueryRepository {

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM exchange_request er WHERE er.item.id = :itemId")
    void deleteAllByItemId(@Param("itemId") Long itemId);

    default ExchangeRequest findByIdOrElseThrow(Long id) {
        return findById(id)
                .orElseThrow(() -> new ServerException(ExceptionCode.EXCHANGE_REQUEST_NOT_FOUND));
    }
}
