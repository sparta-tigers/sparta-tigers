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
public class InningTexts {
    private List<String> inningOneTexts;
    private List<String> inningTwoTexts;
    private List<String> inningThreeTexts;
    private List<String> inningFourTexts;
    private List<String> inningFiveTexts;
    private List<String> inningSixTexts;
    private List<String> inningSevenTexts;
    private List<String> inningEightTexts;
    private List<String> inningNineTexts;
    private List<String> inningExtraTexts;
}
