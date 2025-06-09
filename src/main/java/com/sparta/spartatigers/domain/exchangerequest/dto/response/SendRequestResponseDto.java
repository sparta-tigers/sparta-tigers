package com.sparta.spartatigers.domain.exchangerequest.dto.response;

import java.time.LocalDateTime;

import com.sparta.spartatigers.domain.exchangerequest.model.entity.ExchangeRequest;
import com.sparta.spartatigers.domain.item.model.entity.Item.Category;
import com.sparta.spartatigers.domain.item.model.entity.Item.Status;
import com.sparta.spartatigers.domain.user.dto.UserResponseDto;

public record SendRequestResponseDto(
        Long exchangeRequestId,
        UserResponseDto receiver,
        Category category,
        String title,
        Status status,
        LocalDateTime createdAt) {

    public static SendRequestResponseDto from(ExchangeRequest exchangeRequest) {

        return new SendRequestResponseDto(
                exchangeRequest.getId(),
                UserResponseDto.from(exchangeRequest.getReceiver()),
                exchangeRequest.getItem().getCategory(),
                exchangeRequest.getItem().getTitle(),
                exchangeRequest.getItem().getStatus(),
                exchangeRequest.getCreatedAt());
    }
}
