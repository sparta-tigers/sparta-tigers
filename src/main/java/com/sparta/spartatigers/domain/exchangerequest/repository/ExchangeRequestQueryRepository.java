package com.sparta.spartatigers.domain.exchangerequest.repository;

public interface ExchangeRequestQueryRepository {

    boolean findExchangeRequest(Long senderId, Long receiverId, Long itemId);
}
