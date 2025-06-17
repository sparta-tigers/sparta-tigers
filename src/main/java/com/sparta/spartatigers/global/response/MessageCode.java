package com.sparta.spartatigers.global.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MessageCode {

    // 인증/인가

    // 회원

    // 라이브 보드

    // 알람
    ALARM_REQUEST_SUCCESS("알람 요청을 성공적으로 보냈습니다."),
    // 채팅방

    // 교환 요청
    EXCHANGE_REQUEST_SUCCESS("교환 요청을 성공적으로 보냈습니다."),
    UPDATE_EXCHANGE_REQUEST_SUCCESS("교환 요청 상태 변경을 성공했습니다."),
    COMPLETE_EXCHANGE_SUCCESS("교환을 성공적으로 완료했습니다."),

// 아이템

// 직관 기록

// 공통
;
    private final String message;
}
