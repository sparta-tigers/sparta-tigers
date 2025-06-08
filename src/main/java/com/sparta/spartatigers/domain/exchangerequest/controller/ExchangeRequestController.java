package com.sparta.spartatigers.domain.exchangerequest.controller;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import com.sparta.spartatigers.domain.exchangerequest.dto.request.ExchangeRequestDto;
import com.sparta.spartatigers.domain.exchangerequest.dto.response.ReceiveRequestResponseDto;
import com.sparta.spartatigers.domain.exchangerequest.dto.response.SendRequestResponseDto;
import com.sparta.spartatigers.domain.exchangerequest.service.ExchangeRequestService;
import com.sparta.spartatigers.domain.user.model.CustomUserPrincipal;
import com.sparta.spartatigers.global.response.ApiResponse;
import com.sparta.spartatigers.global.response.MessageCode;

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

        return ApiResponse.created(MessageCode.EXCHANGE_REQUEST_SUCCESS.getMessage());
    }

    @GetMapping("/send")
    public ApiResponse<Page<SendRequestResponseDto>> findAllSendRequest(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @PageableDefault(sort = "createdAt", direction = Direction.DESC) Pageable pageable) {

        Page<SendRequestResponseDto> response =
                exchangeRequestService.findAllSendRequest(principal, pageable);

        return ApiResponse.ok(response);
    }

    @GetMapping("/receive")
    public ApiResponse<Page<ReceiveRequestResponseDto>> findAllReceiveRequest(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @PageableDefault(sort = "createdAt", direction = Direction.DESC) Pageable pageable) {

        Page<ReceiveRequestResponseDto> response =
                exchangeRequestService.findAllReceiveRequest(principal, pageable);

        return ApiResponse.ok(response);
    }
}
