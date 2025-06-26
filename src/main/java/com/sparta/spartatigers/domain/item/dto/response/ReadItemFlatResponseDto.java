package com.sparta.spartatigers.domain.item.dto.response;

import java.time.LocalDateTime;

import com.sparta.spartatigers.domain.item.model.entity.Item.Category;
import com.sparta.spartatigers.domain.item.model.entity.Item.Status;

import com.querydsl.core.annotations.QueryProjection;

public record ReadItemFlatResponseDto(
        Long id,
        Long userId,
        String nickname,
        Category category,
        String title,
        Status status,
        LocalDateTime createdAt) {

    @QueryProjection
    public ReadItemFlatResponseDto(
            Long id,
            Long userId,
            String nickname,
            Category category,
            String title,
            Status status,
            LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.nickname = nickname;
        this.category = category;
        this.title = title;
        this.status = status;
        this.createdAt = createdAt;
    }
}
