package com.sparta.spartatigers.domain.item.controller;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import com.sparta.spartatigers.domain.item.dto.request.CreateItemWithLocationRequestDto;
import com.sparta.spartatigers.domain.item.dto.response.CreateItemResponseDto;
import com.sparta.spartatigers.domain.item.dto.response.ReadItemDetailResponseDto;
import com.sparta.spartatigers.domain.item.dto.response.ReadItemResponseDto;
import com.sparta.spartatigers.domain.item.service.ItemService;
import com.sparta.spartatigers.domain.user.model.CustomUserPrincipal;
import com.sparta.spartatigers.global.response.ApiResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/items")
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ApiResponse<CreateItemResponseDto> createItem(
            @Valid @RequestBody CreateItemWithLocationRequestDto request,
            @AuthenticationPrincipal CustomUserPrincipal userPrincipal) {

        CreateItemResponseDto response = itemService.createItem(request, userPrincipal);

        return ApiResponse.created(response);
    }

    @GetMapping
    public ApiResponse<Page<ReadItemResponseDto>> findAllItems(
            @AuthenticationPrincipal CustomUserPrincipal userPrincipal,
            @PageableDefault(sort = "createdAt", direction = Direction.DESC) Pageable pageable) {

        Page<ReadItemResponseDto> response = itemService.findAllItems(userPrincipal, pageable);

        return ApiResponse.ok(response);
    }

    @GetMapping("/{itemId}")
    public ApiResponse<ReadItemDetailResponseDto> findItemById(@PathVariable Long itemId) {

        ReadItemDetailResponseDto response = itemService.findItemById(itemId);

        return ApiResponse.ok(response);
    }
}
