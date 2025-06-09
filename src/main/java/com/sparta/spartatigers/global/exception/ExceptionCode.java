package com.sparta.spartatigers.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ExceptionCode {

    // 인증/인가
    PASSWORD_NOT_MATCH("비밀번호가 일치하지 않습니다."),
    ALREADY_LOGGED_IN("이미 로그인된 상태입니다."),
    UNAUTHORIZED("로그인 후 이용 가능합니다."),
    FORBIDDEN_REQUEST("권한이 없는 요청입니다."),
    NOT_FOUND_JWT("jwt를 찾을 수 없습니다."),

    // 회원
    EMAIL_ALREADY_USED("이미 사용 중인 이메일입니다."),
    EMAIL_NOT_FOUND("이메일이 존재하지 않습니다."),
    USER_NOT_FOUND("유저가 존재하지 않습니다."),
    ALREADY_DELETED_USER("이미 탈퇴한 회원입니다."),
    NICKNAME_ALREADY_USED("이미 사용 중인 닉네임입니다."),
    ACCESS_DENIED("해당 계정의 접근 권한이 없습니다."),

    // 경기
    MATCH_NOT_FOUND("경기가 존재하지 않습니다."),

    // 라이브 보드

    // 알람

    // 채팅방
    CHATROOM_NOT_FOUND("채팅방이 존재하지 않습니다."),

    // 교환 요청
    EXCHANGE_REQUEST_NOT_FOUND("교환 요청을 찾을 수 없습니동"),

    // 아이템
    ITEM_NOT_FOUND("아이템을 찾을 수 없습니다."),

    // 직관 기록
    WATCH_LIST_NOT_FOUND("직관 기록이 존재하지 않습니다."),

    // 팀
    TEAM_NOT_FOUND("팀이 존재하지 않습니다."),

    // 응원 팀
    DUPLICATE_FAVORITE_TEAM("이미 응원하는 팀입니다."),

    // 공통
    NOT_FOUND("not found ~~"),
    NOT_VALID_EXCEPTION("validation 예외 발생"),
    INTERNAL_SERVER_ERROR("서버 에러 발생");

    private final String message;
}
