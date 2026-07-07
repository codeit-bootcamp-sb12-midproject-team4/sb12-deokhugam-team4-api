package com.codeit.deokhugam.global.exception;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(DeokhugamException.class)
	public ResponseEntity<ErrorResponse> handleDeokhugamException(DeokhugamException ex) {
		log.warn("커스텀 예외 발생: code={}, message={}", ex.getErrorCode(), ex.getMessage());

		int status = ex.getStatus();
		ErrorResponse response = new ErrorResponse(ex, status);
		return ResponseEntity.status(status).body(response);
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
		log.warn("잘못된 요청 예외 발생: message={}", ex.getMessage());

		ErrorCode errorCode = ErrorCode.INVALID_REQUEST;
		ErrorResponse response = new ErrorResponse(ex, errorCode);
		return ResponseEntity.status(errorCode.getStatus()).body(response);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
		log.warn("요청 파라미터 검증 예외 발생: errorCount={}", ex.getBindingResult().getErrorCount());

		Map<String, Object> details = new HashMap<>();
		ex.getBindingResult()
			.getFieldErrors()
			.forEach(e -> details.put(e.getField(), e.getDefaultMessage()));

		ErrorCode errorCode = ErrorCode.INVALID_PARAMETER;
		ErrorResponse response = new ErrorResponse(ex, errorCode, details);
		return ResponseEntity.status(errorCode.getStatus()).body(response);
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ErrorResponse> handleConstraintViolationException(
		ConstraintViolationException exception
	) {

		Map<String, Object> details = new HashMap<>();

		for (ConstraintViolation<?> violation : exception.getConstraintViolations()) {
			details.put(
				violation.getPropertyPath().toString(),
				violation.getMessage()
			);
		}

		ErrorResponse response = new ErrorResponse(
			Instant.now(),
			ErrorCode.INVALID_REQUEST.name(),
			ErrorCode.INVALID_REQUEST.getMessage(),
			details,
			exception.getClass().getSimpleName(),
			ErrorCode.INVALID_REQUEST.getStatus()
		);

		return ResponseEntity
			.status(ErrorCode.INVALID_REQUEST.getStatus())
			.body(response);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleException(Exception ex) {
		log.error("예상치 못한 오류 발생: {}", ex.getMessage(), ex);

		ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
		ErrorResponse response = new ErrorResponse(
			Instant.now(),
			errorCode.name(),
			errorCode.getMessage(),
			new HashMap<>(),
			ex.getClass().getSimpleName(),
			errorCode.getStatus()
		);
		return ResponseEntity.status(errorCode.getStatus()).body(response);
	}
}
