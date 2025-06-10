package com.sparta.spartatigers.domain.item.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import lombok.RequiredArgsConstructor;

import com.sparta.spartatigers.domain.item.model.entity.Item;
import com.sparta.spartatigers.domain.item.model.entity.Item.Status;
import com.sparta.spartatigers.domain.item.model.entity.QItem;
import com.sparta.spartatigers.domain.user.model.entity.QUser;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

@RequiredArgsConstructor
public class ItemQueryRepositoryImpl implements ItemQueryRepository {

    private final JPAQueryFactory queryFactory;
    private final QItem item = QItem.item;
    private final QUser user = QUser.user;

    @Override
    public Page<Item> findAllByStatus(Status status, Pageable pageable) {

        List<Item> itemList =
                queryFactory
                        .selectFrom(item)
                        .where(itemStatusEq(status))
                        .join(item.user, user)
                        .fetchJoin()
                        .orderBy(item.createdAt.desc())
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize())
                        .fetch();

        Long total =
                queryFactory.select(item.count()).from(item).where(itemStatusEq(status)).fetchOne();

        long count = (total == null) ? 0L : total;

        return new PageImpl<>(itemList, pageable, count);
    }

    @Override
    public Optional<Item> findItemById(Long itemId, Status status) {

        return Optional.ofNullable(
                queryFactory
                        .selectFrom(item)
                        .where(itemStatusEq(status), item.id.eq(itemId))
                        .join(item.user, user)
                        .fetchJoin()
                        .fetchOne());
    }

    private BooleanExpression itemStatusEq(Status status) {

        return item.status.eq(status);
    }
}
