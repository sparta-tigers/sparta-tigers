package com.sparta.spartatigers.domain.exchangerequest.dto.response;

import java.time.LocalDateTime;

import com.sparta.spartatigers.domain.exchangerequest.model.entity.ExchangeRequest;
import com.sparta.spartatigers.domain.item.model.entity.Item.Category;
import com.sparta.spartatigers.domain.item.model.entity.Item.Status;
import com.sparta.spartatigers.domain.user.dto.UserResponseDto;

public record ReceiveRequestResponseDto(
        Long exchangeRequestId,
        UserResponseDto sender,
        Category category,
        String title,
        Status status,
        LocalDateTime createdAt) {

    public static ReceiveRequestResponseDto from(ExchangeRequest exchangeRequest) {

        return new ReceiveRequestResponseDto(
                exchangeRequest.getId(),
                UserResponseDto.from(exchangeRequest.getSender()),
                exchangeRequest.getItem().getCategory(),
                exchangeRequest.getItem().getTitle(),
                exchangeRequest.getItem().getStatus(),
                exchangeRequest.getItem().getCreatedAt());
    }
}
