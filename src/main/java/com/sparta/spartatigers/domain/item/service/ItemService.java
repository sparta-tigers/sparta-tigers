package com.sparta.spartatigers.domain.item.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.sparta.spartatigers.domain.item.dto.request.CreateItemRequestDto;
import com.sparta.spartatigers.domain.item.dto.response.CreateItemResponseDto;
import com.sparta.spartatigers.domain.item.model.entity.Item;
import com.sparta.spartatigers.domain.item.repository.ItemRepository;
import com.sparta.spartatigers.domain.user.model.CustomUserPrincipal;
import com.sparta.spartatigers.domain.user.model.entity.User;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    @Transactional
    public CreateItemResponseDto createItem(
            CreateItemRequestDto request, CustomUserPrincipal principal) {

        User user = principal.getUser();

        Item item = Item.of(request, user);
        itemRepository.save(item);

        return CreateItemResponseDto.from(item);
    }
}
