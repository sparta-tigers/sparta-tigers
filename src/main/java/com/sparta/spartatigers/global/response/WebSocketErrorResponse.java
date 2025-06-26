package com.sparta.spartatigers.global.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import com.sparta.spartatigers.global.exception.ExceptionCode;

@Getter
@AllArgsConstructor(staticName = "of")
public class WebSocketErrorResponse {

    private final String errorType;
    private final String message;

    public static WebSocketErrorResponse from(ExceptionCode code) {
        return WebSocketErrorResponse.of(code.getStatus().name(), code.getMessage());
    }
}
