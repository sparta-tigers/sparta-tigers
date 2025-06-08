package com.sparta.spartatigers.domain.exchangerequest.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.sparta.spartatigers.domain.exchangerequest.model.entity.ExchangeRequest;

public interface ExchangeRequestQueryRepository {

    boolean findExchangeRequest(Long senderId, Long receiverId, Long itemId);

    Page<ExchangeRequest> findAllSendRequest(Long senderId, Pageable pageable);

    Page<ExchangeRequest> findAllReceiveRequest(Long receiverId, Pageable pageable);
}
