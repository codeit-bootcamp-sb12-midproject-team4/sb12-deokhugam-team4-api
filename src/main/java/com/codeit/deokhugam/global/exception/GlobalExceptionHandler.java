package com.codeit.deokhugam.global.exception;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.codeit.deokhugam.domain.comment.exception.CommentException;
import com.codeit.deokhugam.domain.notification.exception.NotificationException;
import com.codeit.deokhugam.domain.review.exception.ReviewException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(NotificationException.class)
	public ResponseEntity<ErrorResponse> handleNotificationException(
			NotificationException exception) {
		ErrorResponse response = new ErrorResponse(
				Instant.now(),
				exception.getErrorCode().name(),
				exception.getMessage(),
				exception.getDetails(),
				exception.getClass().getSimpleName(),
				exception.getStatus()
		);
		return ResponseEntity.status(exception.getStatus()).body(response);
	}

	@ExceptionHandler(CommentException.class)
	public ResponseEntity<ErrorResponse> handleCommentException(
			CommentException exception) {
		ErrorResponse response = new ErrorResponse(
				Instant.now(),
				exception.getErrorCode().name(),
				exception.getMessage(),
				exception.getDetails(),
				exception.getClass().getSimpleName(),
				exception.getStatus()
		);
		return ResponseEntity.status(exception.getStatus()).body(response);
	}

	@ExceptionHandler(ReviewException.class)
	public ResponseEntity<ErrorResponse> handleReviewException(ReviewException exception) {
		ErrorResponse response = new ErrorResponse(
			Instant.now(),
			exception.getErrorCode().name(),
			exception.getMessage(),
			exception.getDetails(),
			exception.getClass().getSimpleName(),
			exception.getStatus()
		);

		return ResponseEntity.status(exception.getStatus()).body(response);
	}

	/*@ExceptionHandler(DashboardException.class)
	public ResponseEntity<ErrorResponse> handleDashboardException(
		DashboardException exception
	) {
		ErrorResponse response = new ErrorResponse(
			Instant.now(),
			exception.getErrorCode().name(),
			exception.getMessage(),
			exception.getDetails(),
			exception.getClass().getSimpleName(),
			exception.getStatus()
		);

		return ResponseEntity.status(exception.getStatus()).body(response);
	}*/

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
}
