package com.sparta.spartatigers.domain.exchangerequest.repository;

import lombok.RequiredArgsConstructor;

import com.sparta.spartatigers.domain.exchangerequest.model.entity.QExchangeRequest;
import com.sparta.spartatigers.domain.item.model.entity.QItem;
import com.sparta.spartatigers.domain.user.model.entity.QUser;

import com.querydsl.jpa.impl.JPAQueryFactory;

@RequiredArgsConstructor
public class ExchangeRequestRepositoryQueryImpl implements ExchangeRequestRepositoryQuery {

    private final JPAQueryFactory queryFactory;
    private final QExchangeRequest exchangeRequest = QExchangeRequest.exchangeRequest;
    private final QItem item = QItem.item;
    private final QUser sender = new QUser("sender");
    private final QUser receiver = new QUser("receiver");

    @Override
    public boolean findExchangeRequest(Long senderId, Long receiverId, Long itemId) {

        Integer result =
                queryFactory
                        .selectOne()
                        .from(exchangeRequest)
                        .join(exchangeRequest.sender, sender)
                        .join(exchangeRequest.receiver, receiver)
                        .join(exchangeRequest.item, item)
                        .where(
                                sender.id.eq(senderId),
                                receiver.id.eq(receiverId),
                                item.id.eq(itemId))
                        .fetchFirst();

        return result != null;
    }
}
