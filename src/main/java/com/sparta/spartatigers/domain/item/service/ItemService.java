package com.sparta.spartatigers.domain.item.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.sparta.spartatigers.domain.item.dto.request.CreateItemWithLocationRequestDto;
import com.sparta.spartatigers.domain.item.dto.response.CreateItemResponseDto;
import com.sparta.spartatigers.domain.item.dto.response.ReadItemDetailResponseDto;
import com.sparta.spartatigers.domain.item.dto.response.ReadItemFlatResponseDto;
import com.sparta.spartatigers.domain.item.dto.response.ReadItemResponseDto;
import com.sparta.spartatigers.domain.item.model.entity.Item;
import com.sparta.spartatigers.domain.item.model.entity.Item.Status;
import com.sparta.spartatigers.domain.item.repository.ItemRepository;
import com.sparta.spartatigers.domain.user.model.CustomUserPrincipal;
import com.sparta.spartatigers.domain.user.model.entity.User;
import com.sparta.spartatigers.global.service.S3Service;
import com.sparta.spartatigers.global.util.S3FolderType;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemService {

    private static final double SEARCH_RADIUS_KM = 0.05;
    private final ItemRepository itemRepository;
    private final LocationService locationService;
    private final S3Service s3Service;

    @Transactional
    public CreateItemResponseDto createItem(
            CreateItemWithLocationRequestDto request,
            MultipartFile file,
            CustomUserPrincipal principal) {

        //        LocationRequestDto locationDto = request.getLocationDto();
        //        boolean isNear =
        //                locationService.isNearStadium(
        //                        locationDto.getLongitude(), locationDto.getLatitude());
        //
        //        if (!isNear) {
        //            throw new ServerException(ExceptionCode.LOCATION_NOT_VALID);
        //        }

        User user = principal.getUser();
        log.debug("[createItem] 사용자 ID: {}", user.getId());

        String image = null;
        try {
            image = s3Service.uploadFile(file, S3FolderType.ITEM, principal.getUser().getId());
            log.debug("[createItem] S3 이미지 업로드 완료: {}", image);
        } catch (Exception e) {
            log.error("[createItem] S3 이미지 업로드 중 예외 발생 - 사용자 ID: {}", user.getId(), e);
        }

        Item item = Item.of(request.getItemDto(), user, image);
        itemRepository.save(item);
        log.info("[createItem] 아이템 저장 완료 - itemId: {}", item.getId());

        ReadItemResponseDto newItemDto = ReadItemResponseDto.from(item);
        locationService.notifyUsersNearBy(user.getId(), "ADD_ITEM", newItemDto);

        return CreateItemResponseDto.from(item);
    }

    @Transactional(readOnly = true)
    public Page<ReadItemResponseDto> findAllItems(
            CustomUserPrincipal principal, Pageable pageable) {

        Long userId = principal.getUser().getId();
        log.debug("[findAllItems] 사용자 ID: {}", userId);
        List<Long> nearByUserIds = locationService.findUsersNearBy(userId, SEARCH_RADIUS_KM);
        log.debug("[findAllItems] 근처 사용자 수: {}", nearByUserIds.size());
        nearByUserIds.add(userId);

        Page<ReadItemFlatResponseDto> itemList =
                itemRepository.findAllByStatus(Status.REGISTERED, nearByUserIds, pageable);
        log.info("[findAllItems] 검색된 아이템 수: {}", itemList.getTotalElements());

        return itemList.map(ReadItemResponseDto::from);
    }

    @Transactional(readOnly = true)
    public ReadItemDetailResponseDto findItemById(Long itemId) {

        Item item = itemRepository.findItemByIdOrElseThrow(itemId);
        log.debug("[findItemById] 아이템 조회 완료 - itemId: {}", item.getId());

        return ReadItemDetailResponseDto.from(item);
    }
}
