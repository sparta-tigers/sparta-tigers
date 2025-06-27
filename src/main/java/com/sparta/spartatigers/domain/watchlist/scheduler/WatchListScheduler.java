package com.sparta.spartatigers.domain.watchlist.scheduler;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import com.sparta.spartatigers.domain.watchlist.model.entity.WatchListFile;
import com.sparta.spartatigers.domain.watchlist.repository.WatchListFileRepository;
import com.sparta.spartatigers.global.util.FileUtil;

@Component
@RequiredArgsConstructor
@Log4j2
public class WatchListScheduler {

    private final WatchListFileRepository watchListFileRepository;
    private final FileUtil fileUtil;

    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void deleteUnusedImages() {
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(30);
        List<WatchListFile> unusedImages =
                watchListFileRepository.findByUsedFalseAndCreatedAtBefore(threshold);

        for (WatchListFile file : unusedImages) {
            try {
                fileUtil.delete(file.getFileName());
                watchListFileRepository.delete(file);
                log.info("S3 및 DB 삭제 완료: {}", file.getFileUrl());
            } catch (Exception e) {
                log.error("이미지 삭제 실패: {}", file.getFileUrl(), e);
            }
        }
    }
}
