package com.sparta.spartatigers.global.exception;

import java.util.List;

import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<ApiResponse<?>> handleValidationException(
            MethodArgumentNotValidException ex) {
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

        ApiResponse<?> response =
                ApiResponse.fail(ExceptionCode.NOT_VALID_EXCEPTION, fieldErrorDetails);
        return ResponseEntity.status(ExceptionCode.NOT_VALID_EXCEPTION.getStatus()).body(response);
    }

    // 커스텀 예외 핸들러
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ApiResponse<?>> handleBaseException(BaseException ex) {
        log.error("Catch Business Exception : ", ex);
        return ResponseEntity.status(ex.getStatus()).body(ApiResponse.fail(ex));
    }

    // 예상치 못한 예외 핸들러
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleGeneralException(Exception e) {
        log.error("Catch General Exception : ", e);
        ServerException serverException = new ServerException(ExceptionCode.INTERNAL_SERVER_ERROR);
        return ResponseEntity.status(serverException.getStatus())
                .body(ApiResponse.fail(serverException));
    }
}
