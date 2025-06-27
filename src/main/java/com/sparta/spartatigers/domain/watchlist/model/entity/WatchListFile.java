package com.sparta.spartatigers.domain.watchlist.model.entity;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.sparta.spartatigers.domain.common.entity.BaseEntity;

@Getter
@Entity(name = "watch_list_file")
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class WatchListFile extends BaseEntity {
    @Column private String fileName;

    @Column private String fileUrl;

    @Column private String originalFileName;
    @Column private Long userId;

    private boolean isUsed = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "watch_list_id")
    private WatchList watchList;

    public static WatchListFile create(
            String fileName, String fileUrl, String originalFileName, Long userId) {
        WatchListFile image = new WatchListFile();
        image.fileName = fileName;
        image.fileUrl = fileUrl;
        image.originalFileName = originalFileName;
        image.userId = userId;
        image.isUsed = false;
        return image;
    }
}
