package com.sparta.spartatigers.domain.item.repository;

import java.util.Optional;

import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import com.sparta.spartatigers.domain.item.model.entity.Item;
import com.sparta.spartatigers.global.exception.ExceptionCode;
import com.sparta.spartatigers.global.exception.ServerException;

public interface ItemRepository extends JpaRepository<Item, Long>, ItemQueryRepository {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select i from items i where i.id = :id")
    Optional<Item> findByIdWithLock(Long id);

    default Item findByIdWithLockOrElseThrow(Long id) {
        return findByIdWithLock(id)
                .orElseThrow(() -> new ServerException(ExceptionCode.ITEM_NOT_FOUND));
    }
}
