package com.sparta.spartatigers.domain.liveboard.model;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LiveBoard {
    private String id;
    private String name;
    private LocalDateTime createdAt;

    public LiveBoard(String name) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.createdAt = LocalDateTime.now();
    }
}
