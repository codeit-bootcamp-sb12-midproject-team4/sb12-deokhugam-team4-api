package com.codeit.deokhugam.global.exception;

import java.time.Instant;

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
}
