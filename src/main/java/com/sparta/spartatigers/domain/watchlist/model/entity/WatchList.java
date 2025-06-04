package com.sparta.spartatigers.domain.watchlist.model.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.sparta.spartatigers.domain.common.entity.BaseEntity;
import com.sparta.spartatigers.domain.match.model.entity.Match;
import com.sparta.spartatigers.domain.user.model.entity.User;
import com.sparta.spartatigers.domain.watchlist.dto.request.CreateWatchListRequestDto;

@Getter
@Entity(name = "watch_list")
@NoArgsConstructor
@AllArgsConstructor
public class WatchList extends BaseEntity {

    @Column private String contents;

    @Column private int rating;

    @Column private LocalDateTime deletedAt;

    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @JoinColumn(name = "match_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Match match;

    public static WatchList from(Match match, CreateWatchListRequestDto dto) {
        return new WatchList(
                dto.getRecord().getContent(), dto.getRecord().getRate(), null, null, match);
    }

    public void deleted() {
        this.deletedAt = LocalDateTime.now();
    }
}
