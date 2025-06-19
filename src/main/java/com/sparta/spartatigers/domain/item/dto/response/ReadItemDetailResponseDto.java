package com.sparta.spartatigers.domain.item.dto.response;

import java.time.LocalDateTime;

import com.sparta.spartatigers.domain.item.model.entity.Item;
import com.sparta.spartatigers.domain.item.model.entity.Item.Category;
import com.sparta.spartatigers.domain.item.model.entity.Item.Status;
import com.sparta.spartatigers.domain.user.dto.response.UserResponseDto;

public record ReadItemDetailResponseDto(
        Long id,
        UserResponseDto user,
        Category category,
        String image,
        String seatInfo,
        String title,
        String description,
        Status status,
        LocalDateTime createdAt) {

    public static ReadItemDetailResponseDto from(Item item) {

        return new ReadItemDetailResponseDto(
                item.getId(),
                UserResponseDto.from(item.getUser()),
                item.getCategory(),
                item.getImage(),
                item.getSeatInfo(),
                item.getTitle(),
                item.getDescription(),
                item.getStatus(),
                item.getCreatedAt());
    }
}
