package com.sparta.spartatigers.global.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ExceptionCode {

    // 인증 인가
    PASSWORD_NOT_MATCH("비밀번호가 일치하지 않습니다.", HttpStatus.UNAUTHORIZED),
    ALREADY_LOGGED_IN("이미 로그인된 상태입니다.", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED("로그인 후 이용 가능합니다.", HttpStatus.UNAUTHORIZED),
    FORBIDDEN_REQUEST("권한이 없는 요청입니다.", HttpStatus.FORBIDDEN),
    NOT_FOUND_JWT("jwt를 찾을 수 없습니다.", HttpStatus.UNAUTHORIZED),
    NOT_SUPPORTED_SOCIAL_LOGIN("지원하지 않는 소셜 로그인입니다.", HttpStatus.BAD_REQUEST),

    // 회원
    EMAIL_ALREADY_USED("이미 사용 중인 이메일입니다.", HttpStatus.CONFLICT),
    EMAIL_NOT_FOUND("이메일이 존재하지 않습니다.", HttpStatus.NOT_FOUND),
    USER_NOT_FOUND("유저가 존재하지 않습니다.", HttpStatus.NOT_FOUND),
    ALREADY_DELETED_USER("이미 탈퇴한 회원입니다.", HttpStatus.BAD_REQUEST),
    NICKNAME_ALREADY_USED("이미 사용 중인 닉네임입니다.", HttpStatus.CONFLICT),
    ACCESS_DENIED("해당 계정의 접근 권한이 없습니다.", HttpStatus.FORBIDDEN),

    // 매치
    MATCH_NOT_FOUND("경기 일정을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),

    // 알람
    ALARM_NOT_FOUND("알람을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),

    // 채팅방
    CHATROOM_NOT_FOUND("채팅방이 존재하지 않습니다.", HttpStatus.NOT_FOUND),

    // 교환 요청
    EXCHANGE_REQUEST_NOT_FOUND("교환 요청을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    EXCHANGE_REQUEST_DUPLICATED("이미 교환 요청을 보냈습니다.", HttpStatus.CONFLICT),

    // 아이템
    ITEM_NOT_FOUND("아이템을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    CANNOT_REQUEST_OWN_ITEM("자신의 아이템에 대해 교환 요청을 할 수 없습니다.", HttpStatus.BAD_REQUEST),
    RECEIVER_NOT_OWNER("해당 아이템의 소유자에게만 요청을 보낼 수 있습니다.", HttpStatus.FORBIDDEN),
    RECEIVER_FORBIDDEN("요청을 받은 사용자만 요청을 수정할 수 있습니다.", HttpStatus.FORBIDDEN),
    LOCATION_NOT_VALID("아이템은 야구장 근처에서만 등록할 수 있습니다.", HttpStatus.BAD_REQUEST),

    // 직관 기록
    WATCH_LIST_NOT_FOUND("직관 기록이 존재하지 않습니다.", HttpStatus.NOT_FOUND),

    // 팀
    TEAM_NOT_FOUND("팀이 존재하지 않습니다.", HttpStatus.NOT_FOUND),

    // 응원 팀
    FAVORITE_TEAM_NOT_FOUND("응원하는 팀이 없습니다.", HttpStatus.NOT_FOUND),
    ALREADY_EXISTS_FAVORITE_TEAM("이미 응원하는 팀이 있습니다.", HttpStatus.CONFLICT),

    // 공통
    NOT_FOUND("not found ~~", HttpStatus.NOT_FOUND),
    NOT_VALID_EXCEPTION("validation 예외 발생", HttpStatus.BAD_REQUEST),
    INTERNAL_SERVER_ERROR("서버 에러 발생", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String message;
    private final HttpStatus status;
}
