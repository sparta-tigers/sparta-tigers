package com.sparta.spartatigers.global.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class InvalidRequestException extends BaseException {

    private final ExceptionCode exceptionCode;

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.BAD_REQUEST;
    }

    @Override
    public String getMessage() {
        return exceptionCode.getMessage();
    }
}
