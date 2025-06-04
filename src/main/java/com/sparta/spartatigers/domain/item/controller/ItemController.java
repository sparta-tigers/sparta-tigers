package com.sparta.spartatigers.domain.item.controller;

import com.sparta.spartatigers.domain.item.dto.request.CreateItemRequestDto;
import com.sparta.spartatigers.domain.item.service.ItemService;
import com.sparta.spartatigers.domain.user.model.CustomUserPrincipal;
import com.sparta.spartatigers.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/items")
public class ItemController {

	private final ItemService itemService;

	@PostMapping
	public ApiResponse<Void> createItem(
		@Valid @RequestBody CreateItemRequestDto request,
		@AuthenticationPrincipal CustomUserPrincipal userPrincipal) {

		itemService.createItem(request, userPrincipal);

		return ApiResponse.created(null);
	}
}
