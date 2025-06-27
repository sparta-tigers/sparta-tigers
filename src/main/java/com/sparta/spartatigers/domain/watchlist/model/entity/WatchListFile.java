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

    private boolean used = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "watch_list_id")
    private WatchList watchList;

    public static WatchListFile create(
            String fileName, String fileUrl, String originalFileName, Long userId) {
        WatchListFile file = new WatchListFile();
        file.fileName = fileName;
        file.fileUrl = fileUrl;
        file.originalFileName = originalFileName;
        file.userId = userId;
        file.used = false;
        return file;
    }

    public void markUsed() {
        this.used = true;
    }
}
