package com.sparta.spartatigers.domain.item.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.sparta.spartatigers.domain.item.dto.request.CreateItemWithLocationRequestDto;
import com.sparta.spartatigers.domain.item.dto.request.LocationRequestDto;
import com.sparta.spartatigers.domain.item.dto.response.CreateItemResponseDto;
import com.sparta.spartatigers.domain.item.dto.response.ReadItemDetailResponseDto;
import com.sparta.spartatigers.domain.item.dto.response.ReadItemResponseDto;
import com.sparta.spartatigers.domain.item.model.entity.Item;
import com.sparta.spartatigers.domain.item.model.entity.Item.Status;
import com.sparta.spartatigers.domain.item.repository.ItemRepository;
import com.sparta.spartatigers.domain.user.model.CustomUserPrincipal;
import com.sparta.spartatigers.domain.user.model.entity.User;
import com.sparta.spartatigers.global.exception.ExceptionCode;
import com.sparta.spartatigers.global.exception.ServerException;

@Service
@RequiredArgsConstructor
public class ItemService {

    private static final double SEARCH_RADIUS_KM = 0.05;
    private final ItemRepository itemRepository;
    private final LocationService locationService;

    @Transactional
    public CreateItemResponseDto createItem(
            CreateItemWithLocationRequestDto request, CustomUserPrincipal principal) {

        LocationRequestDto locationDto = request.getLocationDto();
        boolean isNear =
                locationService.isNearStadium(
                        locationDto.getLongitude(), locationDto.getLatitude());

        if (!isNear) {
            throw new ServerException(ExceptionCode.LOCATION_NOT_VALID);
        }

        User user = principal.getUser();

        Item item = Item.of(request.getItemDto(), user);
        itemRepository.save(item);

        ReadItemResponseDto newItemDto = ReadItemResponseDto.from(item);
        locationService.notifyUsersNearBy(user.getId(), "ADD_ITEM", newItemDto);

        return CreateItemResponseDto.from(item);
    }

    @Transactional(readOnly = true)
    public Page<ReadItemResponseDto> findAllItems(
            CustomUserPrincipal principal, Pageable pageable) {

        Long userId = principal.getUser().getId();

        List<Long> nearByUserIds = locationService.findUsersNearBy(userId, SEARCH_RADIUS_KM);
        nearByUserIds.add(userId);

        Page<Item> itemList =
                itemRepository.findAllByStatus(Status.REGISTERED, nearByUserIds, pageable);

        return itemList.map(ReadItemResponseDto::from);
    }

    @Transactional(readOnly = true)
    public ReadItemDetailResponseDto findItemById(Long itemId) {

        Item item = itemRepository.findItemByIdOrElseThrow(itemId);

        return ReadItemDetailResponseDto.from(item);
    }
}
