package com.sparta.spartatigers.domain.watchlist.dto;

import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MatchDto {

    @NotNull(message = "경기 ID는 비어있을 수 없습니다.")
    private Long id;
}
