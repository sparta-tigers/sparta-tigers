package com.sparta.spartatigers.domain.exchangerequest.repository;

public interface ExchangeRequestRepositoryQuery {

    boolean findExchangeRequest(Long senderId, Long receiverId, Long itemId);
}
