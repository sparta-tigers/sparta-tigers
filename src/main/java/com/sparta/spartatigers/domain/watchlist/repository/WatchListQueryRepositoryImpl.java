package com.sparta.spartatigers.domain.watchlist.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

import com.sparta.spartatigers.domain.match.model.entity.QMatch;
import com.sparta.spartatigers.domain.team.model.entity.QStadium;
import com.sparta.spartatigers.domain.team.model.entity.QTeam;
import com.sparta.spartatigers.domain.watchlist.model.entity.QWatchList;
import com.sparta.spartatigers.domain.watchlist.model.entity.WatchList;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

@Repository
@RequiredArgsConstructor
public class WatchListQueryRepositoryImpl implements WatchListQueryRepository {

    private final JPAQueryFactory queryFactory;

    QWatchList watchList = QWatchList.watchList;
    QMatch match = QMatch.match;
    QTeam homeTeam = new QTeam("homeTeam");
    QTeam awayTeam = new QTeam("awayTeam");
    QStadium stadium = QStadium.stadium;

    @Override
    public Page<WatchList> findAllByUserIdWithMatchDetails(Long userId, Pageable pageable) {
        List<WatchList> results =
                baseQueryWithMatchDetails()
                        .where(isUserOwner(userId))
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize())
                        .fetch();

        Long total =
                queryFactory
                        .select(watchList.count())
                        .from(watchList)
                        .where(isUserOwner(userId))
                        .fetchOne();

        return new PageImpl<>(results, pageable, total != null ? total : 0);
    }

    @Override
    public Optional<WatchList> findByIdWithMatchDetails(Long watchListId, Long userId) {
        WatchList result =
                baseQueryWithMatchDetails()
                        .where(isUserOwner(userId), hasWatchListId(watchListId))
                        .fetchOne();

        return Optional.ofNullable(result);
    }

    /** 공통 조인 + fetchJoin 처리 메서드 */
    private JPAQuery<WatchList> baseQueryWithMatchDetails() {
        return queryFactory
                .selectFrom(watchList)
                .join(watchList.match, match)
                .fetchJoin()
                .join(match.homeTeam, homeTeam)
                .fetchJoin()
                .join(match.awayTeam, awayTeam)
                .fetchJoin()
                .join(match.stadium, stadium)
                .fetchJoin();
    }

    private BooleanExpression isUserOwner(Long userId) {
        return watchList.user.id.eq(userId);
    }

    private BooleanExpression hasWatchListId(Long watchListId) {
        return watchList.id.eq(watchListId);
    }
}
