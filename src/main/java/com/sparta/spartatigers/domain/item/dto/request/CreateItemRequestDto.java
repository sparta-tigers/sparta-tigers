package com.sparta.spartatigers.domain.item.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import com.sparta.spartatigers.domain.item.model.entity.Item;
import com.sparta.spartatigers.domain.item.model.entity.Item.Category;
import com.sparta.spartatigers.domain.item.model.entity.Item.Status;
import com.sparta.spartatigers.domain.user.model.entity.User;

public record CreateItemRequestDto(
        @NotNull(message = "카테고리는 필수입니다.") Category category,
        String image,
        @NotBlank(message = "제목은 필수입니다.") String title,
        String seatInfo,
        String description) {

    public Item toEntity(User user) {
        return Item.builder()
                .category(category)
                .image(image)
                .title(title)
                .seatInfo(seatInfo)
                .description(description)
                .status(Status.REGISTERED)
                .user(user)
                .build();
    }
}
