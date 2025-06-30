package com.sparta.spartatigers.domain.liveboard.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GameMessage {
    private Long matchId;
    private List<GamePlayer> players;
    private MatchScore matchScore;
}
