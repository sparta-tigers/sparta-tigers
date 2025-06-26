package com.sparta.spartatigers.global.exception;

import java.util.List;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;

import com.sparta.spartatigers.global.response.ApiResponse;
import com.sparta.spartatigers.global.response.ErrorResponse;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // validation 예외 핸들러
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<?> handleValidationException(MethodArgumentNotValidException ex) {
        log.error("Catch Validation Exception: {}", ex.getMessage());

        List<ErrorResponse.FieldErrorDetail> fieldErrorDetails =
                ex.getBindingResult().getFieldErrors().stream()
                        .map(
                                error ->
                                        ErrorResponse.FieldErrorDetail.of(
                                                error.getField(),
                                                error.getRejectedValue(),
                                                error.getDefaultMessage()))
                        .toList();

        return ApiResponse.fail(ExceptionCode.NOT_VALID_EXCEPTION, fieldErrorDetails);
    }

    // 커스텀 예외 핸들러
    @ExceptionHandler(BaseException.class)
    public ApiResponse<?> handleBaseException(BaseException ex) {
        log.error("Catch Business Exception : ", ex);
        return ApiResponse.fail(ex);
    }

    // 예상치 못한 예외 핸들러
    @ExceptionHandler(Exception.class)
    public ApiResponse<?> handleGeneralException(Exception e) {
        log.error("Catch General Exception : ", e);
        return ApiResponse.fail(new ServerException(ExceptionCode.INTERNAL_SERVER_ERROR));
    }
}
