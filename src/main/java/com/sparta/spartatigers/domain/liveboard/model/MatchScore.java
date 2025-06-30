package com.sparta.spartatigers.domain.liveboard.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MatchScore {
    private Long strike = 0L;
    private Long ball = 0L;
    private Long out = 0L;
}
