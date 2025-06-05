package com.sparta.spartatigers.domain.item.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.sparta.spartatigers.domain.item.model.entity.Item;
import com.sparta.spartatigers.domain.item.model.entity.Item.Status;
import com.sparta.spartatigers.global.exception.ExceptionCode;
import com.sparta.spartatigers.global.exception.ServerException;

public interface ItemRepositoryQuery {

    Page<Item> findAllByStatus(Status status, Pageable pageable);

    Optional<Item> findItemById(Long itemId, Status status);

    default Item findItemByIdOrElseThrow(Long itemId) {
        return findItemById(itemId, Status.REGISTERED)
                .orElseThrow(() -> new ServerException(ExceptionCode.ITEM_NOT_FOUND));
    }
}
