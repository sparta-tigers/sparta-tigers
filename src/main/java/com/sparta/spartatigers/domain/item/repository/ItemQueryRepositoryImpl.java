package com.sparta.spartatigers.domain.item.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import lombok.RequiredArgsConstructor;

import com.sparta.spartatigers.domain.item.dto.response.QReadItemFlatResponseDto;
import com.sparta.spartatigers.domain.item.dto.response.ReadItemFlatResponseDto;
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
    public Page<ReadItemFlatResponseDto> findAllByStatus(
            Status status, List<Long> nearByUserIds, Pageable pageable) {

        if (nearByUserIds.isEmpty()) {
            return Page.empty(pageable);
        }

        List<ReadItemFlatResponseDto> itemList =
                queryFactory
                        .select(
                                new QReadItemFlatResponseDto(
                                        item.id,
                                        user.id,
                                        user.nickname,
                                        item.category,
                                        item.title,
                                        item.status,
                                        item.createdAt))
                        .from(item)
                        .where(
                                itemStatusEq(status),
                                item.user.id.in(nearByUserIds),
                                itemCreatedDateEq(LocalDate.now()))
                        .join(item.user, user)
                        .orderBy(item.createdAt.desc())
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize())
                        .fetch();

        Long total =
                queryFactory
                        .select(item.count())
                        .from(item)
                        .join(item.user, user)
                        .where(
                                itemStatusEq(status),
                                item.user.id.in(nearByUserIds),
                                itemCreatedDateEq(LocalDate.now()))
                        .fetchOne();

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

    @Override
    public List<Item> findUncompletedItems(LocalDate yesterDay) {

        return queryFactory
                .selectFrom(item)
                .where(itemStatusEq(Status.REGISTERED), itemCreatedDateEq(yesterDay))
                .fetch();
    }

    private BooleanExpression itemStatusEq(Status status) {

        return item.status.eq(status);
    }

    private BooleanExpression itemCreatedDateEq(LocalDate date) {

        return item.createdDate.eq(date);
    }
}
