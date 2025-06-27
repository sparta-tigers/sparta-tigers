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

    private static final QWatchList watchList = QWatchList.watchList;
    private static final QMatch match = QMatch.match;
    private static final QTeam homeTeam = new QTeam("homeTeam");
    private static final QTeam awayTeam = new QTeam("awayTeam");
    private static final QStadium stadium = QStadium.stadium;

    @Override
    public Page<WatchList> findAllByUserIdWithMatchDetails(Long userId, Pageable pageable) {

        List<WatchList> results =
                baseQueryWithMatchDetails()
                        .where(isUserOwner(userId))
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize())
                        .orderBy(watchList.createdAt.desc())
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

    @Override
    public Page<WatchList> findAllByKeyword(
            Long userId, String teamName, String stadiumName, Pageable pageable) {
        List<WatchList> results =
                baseQueryWithMatchDetails()
                        .where(
                                isUserOwner(userId),
                                containsTeamName(teamName),
                                containsStadiumName(stadiumName))
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize())
                        .fetch();

        Long total =
                queryFactory
                        .select(watchList.count())
                        .from(watchList)
                        .where(
                                isUserOwner(userId),
                                containsTeamName(teamName),
                                containsStadiumName(stadiumName))
                        .fetchOne();

        return new PageImpl<>(results, pageable, total != null ? total : 0);
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

    private BooleanExpression containsTeamName(String teamName) {
        if (teamName == null || teamName.isBlank()) {
            return null;
        }
        // 해당 기록의 홈/어웨이 팀명 모두 조건 검색
        return watchList
                .match
                .awayTeam
                .name
                .containsIgnoreCase(teamName)
                .or(watchList.match.homeTeam.name.containsIgnoreCase(teamName));
    }

    private BooleanExpression containsStadiumName(String stadiumName) {
        if (stadiumName == null || stadiumName.isBlank()) {
            return null;
        }
        return watchList.match.stadium.name.containsIgnoreCase(stadiumName);
    }
}
