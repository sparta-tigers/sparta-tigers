package com.sparta.spartatigers.domain.item.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.sparta.spartatigers.domain.item.dto.response.ReadItemFlatResponseDto;
import com.sparta.spartatigers.domain.item.model.entity.Item;
import com.sparta.spartatigers.domain.item.model.entity.Item.Status;
import com.sparta.spartatigers.global.exception.ExceptionCode;
import com.sparta.spartatigers.global.exception.ServerException;

public interface ItemQueryRepository {

    Page<ReadItemFlatResponseDto> findAllByStatus(
            Status status, List<Long> nearByUserIds, Pageable pageable);

    Optional<Item> findItemById(Long itemId, Status status);

    List<Item> findUncompletedItems(LocalDate yesterDay);

    default Item findItemByIdOrElseThrow(Long itemId) {
        return findItemById(itemId, Status.REGISTERED)
                .orElseThrow(() -> new ServerException(ExceptionCode.ITEM_NOT_FOUND));
    }
}
