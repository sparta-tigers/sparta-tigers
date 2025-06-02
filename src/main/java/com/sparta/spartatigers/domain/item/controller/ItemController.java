package com.sparta.spartatigers.domain.item.controller;

import com.sparta.spartatigers.domain.item.dto.request.CreateItemRequestDto;
import com.sparta.spartatigers.domain.item.service.ItemService;
import com.sparta.spartatigers.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/exchanges")
public class ItemController {

	private final ItemService itemService;

	// TODO 로그인이 구현되면 유저 정보 추가
	@PostMapping
	public ApiResponse<Void> createItem(@Valid @RequestBody CreateItemRequestDto request) {

		itemService.createItem(request);

		return ApiResponse.created(null);
	}
}
