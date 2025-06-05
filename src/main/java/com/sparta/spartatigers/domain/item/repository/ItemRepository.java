package com.sparta.spartatigers.domain.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sparta.spartatigers.domain.item.model.entity.Item;

public interface ItemRepository extends JpaRepository<Item, Long>, ItemRepositoryQuery {}
