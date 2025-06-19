package com.sparta.spartatigers.domain.item.scheduler;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.sparta.spartatigers.domain.item.model.entity.Item;
import com.sparta.spartatigers.domain.item.repository.ItemRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class ItemScheduler {

    private static final String LOCK_KEY = "lock:itemScheduler";
    private final RedissonClient redissonClient;
    private final ItemRepository itemRepository;

    @Transactional
    @Scheduled(cron = "0 0 0 * * *")
    public void failItem() {
        RLock lock = redissonClient.getLock(LOCK_KEY);
        boolean isLocked = false;

        try {
            isLocked = lock.tryLock(10, 60, TimeUnit.SECONDS);
            if (!isLocked) {
                return;
            }

            LocalDate today = LocalDate.now();
            List<Item> itemList = itemRepository.findUncompletedItems(today.minusDays(1));

            for (Item item : itemList) {
                try {
                    item.fail();
                } catch (ObjectOptimisticLockingFailureException e) {
                    log.warn("아이템 ID {} 실패 처리 중 충돌 발생. 작업을 건너뜀.", item.getId());
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            if (isLocked && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
