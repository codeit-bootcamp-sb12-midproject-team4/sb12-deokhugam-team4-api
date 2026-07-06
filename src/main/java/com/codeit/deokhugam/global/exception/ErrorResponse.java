package com.codeit.deokhugam.global.exception;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

// GlobalExceptionHandler에서 에러 응답 바디로 사용
// timestamp, code, message, details, exceptionType, status 포함한 구조화된 에러 응답
@Getter
@RequiredArgsConstructor
public class ErrorResponse {
	private final Instant timestamp;
	private final String code;
	private final String message;
	private final Map<String, Object> details;  // 필드별 상세 오류 정보
	private final String exceptionType;         // 예외 클래스명
	private final int status;                   // HTTP 상태 코드

	// BaseException (커스텀 예외) 로부터 생성
	public ErrorResponse(DeokhugamException exception, int status) {
		this(
			Instant.now(),
			exception.getErrorCode().name(),
			exception.getMessage(),
			exception.getDetails(),
			exception.getClass().getSimpleName(),
			status
		);
	}

	// IllegalArgument 예외에서 사용중, 내부 에러는 생성자로 직접 생성.
	public ErrorResponse(Exception exception, ErrorCode errorCode) {
		this(
			Instant.now(),
			errorCode.name(),
			exception.getMessage(),
			new HashMap<>(),
			exception.getClass().getSimpleName(),
			errorCode.getStatus()
		);
	}

	public ErrorResponse(Exception exception, ErrorCode errorCode, Map<String, Object> details) {
		this(
			Instant.now(),
			errorCode.name(),
			errorCode.getMessage(),
			details,
			exception.getClass().getSimpleName(),
			errorCode.getStatus()
		);
	}
}
