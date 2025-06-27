package com.sparta.spartatigers.domain.watchlist.scheduler;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import com.sparta.spartatigers.domain.watchlist.model.entity.WatchListFile;
import com.sparta.spartatigers.domain.watchlist.repository.WatchListFileRepository;
import com.sparta.spartatigers.global.service.S3Service;

@Component
@RequiredArgsConstructor
@Log4j2
public class WatchListScheduler {

    private final WatchListFileRepository watchListFileRepository;
    private final S3Service s3Service;

    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void deleteUnusedImages() {
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(30);
        List<WatchListFile> unusedImages =
                watchListFileRepository.findByUsedFalseAndCreatedAtBefore(threshold);

        List<WatchListFile> successfullyDeleted = new ArrayList<>();
        for (WatchListFile file : unusedImages) {
            try {
                s3Service.delete(file.getFileName());
                successfullyDeleted.add(file);
                log.info("S3 및 DB 삭제 완료: {}", file.getFileUrl());
            } catch (Exception e) {
                log.error("이미지 삭제 실패: {}", file.getFileUrl(), e);
            }
        }
        if (!successfullyDeleted.isEmpty()) {
            deleteFromDatabase(successfullyDeleted);
        }
    }

    @Transactional
    void deleteFromDatabase(List<WatchListFile> files) {
        watchListFileRepository.deleteAll(files);
        log.info("DB에서 {} 개 파일 삭제 완료", files.size());
    }
}
