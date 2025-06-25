package com.sparta.spartatigers.domain.exchangerequest.dto.request;

import jakarta.validation.constraints.NotNull;

import com.sparta.spartatigers.domain.exchangerequest.model.entity.ExchangeRequest;

public record UpdateExchangeRequestDto(
        @NotNull(message = "변경할 상태값은 필수입니다.") ExchangeRequest.ExchangeStatus status) {}
