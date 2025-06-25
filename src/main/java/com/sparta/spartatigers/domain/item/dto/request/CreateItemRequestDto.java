package com.sparta.spartatigers.domain.item.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import com.sparta.spartatigers.domain.item.model.entity.Item.Category;

public record CreateItemRequestDto(
        @NotNull(message = "카테고리는 필수입니다.") Category category,
        @NotBlank(message = "제목은 필수입니다.") String title,
        String seatInfo,
        String description) {}
