package com.sparta.spartatigers.domain.exchangerequest.controller;

import jakarta.validation.Valid;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import com.sparta.spartatigers.domain.exchangerequest.dto.request.ExchangeRequestDto;
import com.sparta.spartatigers.domain.exchangerequest.service.ExchangeRequestService;
import com.sparta.spartatigers.domain.user.model.CustomUserPrincipal;
import com.sparta.spartatigers.global.response.ApiResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/exchanges/request")
public class ExchangeRequestController {

    private final ExchangeRequestService exchangeRequestService;

    @PostMapping
    public ApiResponse<String> createExchangeRequest(
            @Valid @RequestBody ExchangeRequestDto request,
            @AuthenticationPrincipal CustomUserPrincipal principal) {

        exchangeRequestService.createExchangeRequest(request, principal);

        return ApiResponse.created("교환 요청을 성공적으로 보냈습니다.");
    }
}
