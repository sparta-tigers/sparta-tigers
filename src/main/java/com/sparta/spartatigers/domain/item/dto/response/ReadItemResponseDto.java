package com.sparta.spartatigers.domain.item.dto.response;

import java.time.LocalDateTime;

import com.sparta.spartatigers.domain.item.model.entity.Item;
import com.sparta.spartatigers.domain.item.model.entity.Item.Category;
import com.sparta.spartatigers.domain.item.model.entity.Item.Status;

public record ReadItemResponseDto(
        Long id,
        UserDto user,
        Category category,
        String title,
        Status status,
        LocalDateTime createdAt) {

    public static ReadItemResponseDto from(Item item) {

        return new ReadItemResponseDto(
                item.getId(),
                UserDto.from(item.getUser()),
                item.getCategory(),
                item.getTitle(),
                item.getStatus(),
                item.getCreatedAt());
    }
}
