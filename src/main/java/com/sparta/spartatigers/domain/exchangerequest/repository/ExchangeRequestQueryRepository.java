package com.sparta.spartatigers.domain.exchangerequest.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.sparta.spartatigers.domain.exchangerequest.model.entity.ExchangeRequest;
import com.sparta.spartatigers.domain.exchangerequest.model.entity.ExchangeRequest.ExchangeStatus;
import com.sparta.spartatigers.global.exception.ExceptionCode;
import com.sparta.spartatigers.global.exception.ServerException;

public interface ExchangeRequestQueryRepository {

    boolean findExchangeRequest(Long senderId, Long receiverId, Long itemId);

    Page<ExchangeRequest> findAllSendRequest(Long senderId, Pageable pageable);

    Page<ExchangeRequest> findAllReceiveRequest(Long receiverId, Pageable pageable);

    Optional<ExchangeRequest> findExchangeRequestById(
            Long exchangeRequestId, ExchangeStatus status);

    List<Long> findAllExchangeRequestIds(Long itemId);

    void deleteAllByItemId(Long itemId);

    default ExchangeRequest findExchangeRequestByIdOrElseThrow(Long exchangeRequestId) {
        return findExchangeRequestById(exchangeRequestId, ExchangeStatus.PENDING)
                .orElseThrow(() -> new ServerException(ExceptionCode.EXCHANGE_REQUEST_NOT_FOUND));
    }

    default ExchangeRequest findAcceptedRequestByIdOrElseThrow(Long exchangeRequestId) {
        return findExchangeRequestById(exchangeRequestId, ExchangeStatus.ACCEPTED)
                .orElseThrow(() -> new ServerException(ExceptionCode.EXCHANGE_REQUEST_NOT_FOUND));
    }
}
