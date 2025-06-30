package com.sparta.spartatigers.domain.exchangerequest.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import lombok.RequiredArgsConstructor;

import com.sparta.spartatigers.domain.exchangerequest.model.entity.ExchangeRequest;
import com.sparta.spartatigers.domain.exchangerequest.model.entity.ExchangeRequest.ExchangeStatus;
import com.sparta.spartatigers.domain.exchangerequest.model.entity.QExchangeRequest;
import com.sparta.spartatigers.domain.item.model.entity.QItem;
import com.sparta.spartatigers.domain.user.model.entity.QUser;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

@RequiredArgsConstructor
public class ExchangeRequestQueryRepositoryImpl implements ExchangeRequestQueryRepository {

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
                        .where(senderIdEq(senderId), receiverIdEq(receiverId), itemIdEq(itemId))
                        .fetchFirst();

        return result != null;
    }

    @Override
    public Page<ExchangeRequest> findAllSendRequest(Long senderId, Pageable pageable) {

        List<ExchangeRequest> exchangeRequestList =
                queryFactory
                        .selectFrom(exchangeRequest)
                        .join(exchangeRequest.sender, sender)
                        .fetchJoin()
                        .join(exchangeRequest.receiver, receiver)
                        .fetchJoin()
                        .join(exchangeRequest.item, item)
                        .fetchJoin()
                        .where(senderIdEq(senderId))
                        .orderBy(exchangeRequest.createdAt.desc())
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize())
                        .fetch();

        Long total =
                queryFactory
                        .select(exchangeRequest.count())
                        .from(exchangeRequest)
                        .where(senderIdEq(senderId))
                        .fetchOne();

        long count = (total == null) ? 0L : total;

        return new PageImpl<>(exchangeRequestList, pageable, count);
    }

    @Override
    public Page<ExchangeRequest> findAllReceiveRequest(Long receiverId, Pageable pageable) {

        List<ExchangeRequest> exchangeRequestList =
                queryFactory
                        .selectFrom(exchangeRequest)
                        .join(exchangeRequest.sender, sender)
                        .fetchJoin()
                        .join(exchangeRequest.receiver, receiver)
                        .fetchJoin()
                        .join(exchangeRequest.item, item)
                        .fetchJoin()
                        .where(receiverIdEq(receiverId))
                        .orderBy(exchangeRequest.createdAt.desc())
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize())
                        .fetch();

        Long total =
                queryFactory
                        .select(exchangeRequest.count())
                        .from(exchangeRequest)
                        .where(receiverIdEq(receiverId))
                        .fetchOne();

        long count = (total == null) ? 0L : total;

        return new PageImpl<>(exchangeRequestList, pageable, count);
    }

    @Override
    public Optional<ExchangeRequest> findExchangeRequestById(
            Long exchangeRequestId, ExchangeStatus status) {

        return Optional.ofNullable(
                queryFactory
                        .selectFrom(exchangeRequest)
                        .where(
                                exchangeRequestIdEq(exchangeRequestId),
                                exchangeRequestStatusEq(status))
                        .join(exchangeRequest.sender, sender)
                        .fetchJoin()
                        .join(exchangeRequest.receiver, receiver)
                        .fetchJoin()
                        .join(exchangeRequest.item, item)
                        .fetchJoin()
                        .fetchOne());
    }

    @Override
    public List<Long> findAllExchangeRequestIds(Long itemId) {
        return queryFactory
                .select(exchangeRequest.id)
                .from(exchangeRequest)
                .where(exchangeRequestItemIdEq(itemId))
                .fetch();
    }

    @Override
    public void deleteAllByItemId(Long itemId) {
        queryFactory.delete(exchangeRequest).where(exchangeRequestItemIdEq(itemId)).execute();
    }

    private BooleanExpression senderIdEq(Long senderId) {

        return sender.id.eq(senderId);
    }

    private BooleanExpression receiverIdEq(Long receiverId) {

        return receiver.id.eq(receiverId);
    }

    private BooleanExpression itemIdEq(Long itemId) {

        return item.id.eq(itemId);
    }

    private BooleanExpression exchangeRequestIdEq(Long exchangeRequestId) {

        return exchangeRequest.id.eq(exchangeRequestId);
    }

    private BooleanExpression exchangeRequestStatusEq(ExchangeStatus status) {

        return exchangeRequest.status.eq(status);
    }

    private BooleanExpression exchangeRequestItemIdEq(Long itemId) {

        return exchangeRequest.item.id.eq(itemId);
    }
}
