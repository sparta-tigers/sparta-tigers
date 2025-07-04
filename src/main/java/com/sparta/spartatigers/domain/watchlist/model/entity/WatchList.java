package com.sparta.spartatigers.domain.watchlist.model.entity;

import jakarta.persistence.*;

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
    @Column(columnDefinition = "TEXT")
    private String contents;

    @Column private int rating;

    @Column private String seat;

    //    @Column private LocalDateTime deletedAt;

    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @JoinColumn(name = "match_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Match match;

    public static WatchList from(Match match, CreateWatchListRequestDto dto, User user) {
        return new WatchList(
                dto.getRecord().getContent(),
                dto.getRecord().getRate(),
                dto.getSeat(),
                user,
                match);
    }

    public static WatchList of(WatchList watchList) {
        return new WatchList(
                watchList.getContents(),
                watchList.getRating(),
                watchList.getSeat(),
                watchList.getUser(),
                watchList.getMatch());
    }

    //    public void deleted() {
    //        this.deletedAt = LocalDateTime.now();
    //    }

    public void update(String content, Integer rating) {
        if (content != null) this.contents = content;
        if (rating != null) this.rating = rating;
    }
}
