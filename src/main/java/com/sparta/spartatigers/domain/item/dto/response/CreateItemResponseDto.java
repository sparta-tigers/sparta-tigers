package com.sparta.spartatigers.domain.item.dto.response;

import com.sparta.spartatigers.domain.item.model.entity.Item;
import com.sparta.spartatigers.domain.item.model.entity.Item.Category;
import com.sparta.spartatigers.domain.item.model.entity.Item.Status;

public record CreateItemResponseDto(Long id, Category category, String title, Status status) {

    public static CreateItemResponseDto from(Item item) {

        return new CreateItemResponseDto(
                item.getId(), item.getCategory(), item.getTitle(), item.getStatus());
    }
}
