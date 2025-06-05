package com.sparta.spartatigers.domain.item.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.sparta.spartatigers.domain.item.model.entity.Item;
import com.sparta.spartatigers.domain.item.model.entity.Item.Status;

public interface ItemRepositoryQuery {

    Page<Item> findAllByStatus(Status status, Pageable pageable);
}
