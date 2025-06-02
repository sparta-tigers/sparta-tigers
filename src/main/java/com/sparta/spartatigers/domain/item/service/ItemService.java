package com.sparta.spartatigers.domain.item.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.sparta.spartatigers.domain.item.dto.request.CreateItemRequestDto;
import com.sparta.spartatigers.domain.item.model.entity.Item;
import com.sparta.spartatigers.domain.item.repository.ItemRepository;
import com.sparta.spartatigers.domain.user.model.entity.User;
import com.sparta.spartatigers.domain.user.repository.UserRepository;
import com.sparta.spartatigers.global.exception.ExceptionCode;
import com.sparta.spartatigers.global.exception.ServerException;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Transactional
    public void createItem(CreateItemRequestDto request) {

        User user =
                userRepository
                        .findById(1L)
                        .orElseThrow(() -> new ServerException(ExceptionCode.USER_NOT_FOUND));

        // TODO 유저, 위치 정보 추가
        Item item = request.toEntity(user);

        itemRepository.save(item);
    }
}
