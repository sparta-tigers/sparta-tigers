package com.sparta.spartatigers.domain.item.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.sparta.spartatigers.domain.item.dto.request.CreateItemRequestDto;
import com.sparta.spartatigers.domain.item.dto.response.CreateItemResponseDto;
import com.sparta.spartatigers.domain.item.dto.response.ReadItemDetailResponseDto;
import com.sparta.spartatigers.domain.item.dto.response.ReadItemResponseDto;
import com.sparta.spartatigers.domain.item.model.entity.Item;
import com.sparta.spartatigers.domain.item.model.entity.Item.Status;
import com.sparta.spartatigers.domain.item.repository.ItemRepository;
import com.sparta.spartatigers.domain.user.model.CustomUserPrincipal;
import com.sparta.spartatigers.domain.user.model.entity.User;

@Service
@RequiredArgsConstructor
public class ItemService {

    private static final double SEARCH_RADIUS_KM = 0.05;
    private final ItemRepository itemRepository;
    private final LocationService locationService;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public CreateItemResponseDto createItem(
            CreateItemRequestDto request, CustomUserPrincipal principal) {

        User user = principal.getUser();

        Item item = Item.of(request, user);
        itemRepository.save(item);

        ReadItemResponseDto newItemDto = ReadItemResponseDto.from(item);
        List<Long> nearByUserIds = locationService.findUsersNearBy(user.getId(), SEARCH_RADIUS_KM);

        nearByUserIds.forEach(
                targetUserId -> {
                    String destination = "/server/items/user/" + targetUserId;
                    messagingTemplate.convertAndSend(
                            destination, Map.of("type", "ADD_ITEM", "data", newItemDto));
                });

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

    @Transactional
    @Scheduled(cron = "0 0 0 * * *")
    public void failItem() {

        LocalDate today = LocalDate.now();
        List<Item> itemList = itemRepository.findUncompletedItems(today.minusDays(1));

        itemList.forEach(Item::fail);
    }
}
