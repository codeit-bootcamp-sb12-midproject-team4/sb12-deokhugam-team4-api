package com.codeit.deokhugam.global.exception;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

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

	@ExceptionHandler({
		MissingRequestHeaderException.class,
		MissingServletRequestParameterException.class,
		MissingServletRequestPartException.class,
		MethodArgumentTypeMismatchException.class,
		HandlerMethodValidationException.class
	})
	public ResponseEntity<ErrorResponse> handleBadRequestException(Exception ex) {
		ErrorCode errorCode = ErrorCode.INVALID_PARAMETER;
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

	// 임시 Notfound용. 제거 필요
	@ExceptionHandler(NoSuchElementException.class)
	public ResponseEntity<ErrorResponse> handleNoSuchElementException(NoSuchElementException ex) {
		log.warn("잘못된 요청 예외 발생: message={}", ex.getMessage());

		int status = 404;
		ErrorResponse response = new ErrorResponse(
			Instant.now(),
			"RESOURCE_NOT_FOUND",
			ex.getMessage(),
			new HashMap<>(),
			ex.getClass().getSimpleName(),
			status
		);
		return ResponseEntity.status(status).body(response);
	}
}
