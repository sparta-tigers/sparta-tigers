package com.sparta.spartatigers.global.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ServerException extends BaseException {

    private final ExceptionCode exceptionCode;

    @Override
    public HttpStatus getStatus() {
        return exceptionCode.getStatus();
    }

    @Override
    public String getMessage() {
        return exceptionCode.getMessage();
    }
}
