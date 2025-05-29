package com.sparta.spartatigers.global.response;

import java.util.List;

import com.sparta.spartatigers.global.exception.ExceptionCode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ErrorResponse {

	private String message;
	private List<FieldErrorDetail> fieldErrors;

	public ErrorResponse(ExceptionCode exceptionCode) {
		this.message = exceptionCode.getMessage();
	}

	public ErrorResponse(ExceptionCode exceptionCode, List<FieldErrorDetail> fieldErrors) {
		this.message = exceptionCode.getMessage();
		this.fieldErrors = fieldErrors;
	}

	public static ErrorResponse of(ExceptionCode code) {
		return new ErrorResponse(code);
	}

	public static ErrorResponse of(ExceptionCode code, List<FieldErrorDetail> fieldErrors) {
		return new ErrorResponse(code, fieldErrors);
	}

	@Getter
	@AllArgsConstructor(staticName = "of")
	public static class FieldErrorDetail {

		private String field;
		private Object rejectedValue;
		private String reason;
	}
}