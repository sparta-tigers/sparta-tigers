package com.sparta.spartatigers.domain.item.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.sparta.spartatigers.domain.item.dto.request.CreateItemRequestDto;
import com.sparta.spartatigers.domain.item.dto.response.CreateItemResponseDto;
import com.sparta.spartatigers.domain.item.dto.response.ReadItemResponseDto;
import com.sparta.spartatigers.domain.item.model.entity.Item;
import com.sparta.spartatigers.domain.item.model.entity.Item.Status;
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

    @Transactional(readOnly = true)
    public Page<ReadItemResponseDto> findAllItems(
            CustomUserPrincipal principal, Pageable pageable) {

        User user = principal.getUser();
        // TODO 프론트와 연결 후 실시간으로 로그인한 회원과 가까운 위치에 있는 글이 조회되도록 하기

        Page<Item> itemList = itemRepository.findAllByStatus(Status.REGISTERED, pageable);

        return itemList.map(ReadItemResponseDto::from);
    }
}
