package com.codeit.deokhugam.domain.review.exception;

import java.util.UUID;

import com.codeit.deokhugam.global.exception.ErrorCode;

public class ReviewAlreadyExistsException extends ReviewException {

	public ReviewAlreadyExistsException() {
		super(ErrorCode.REVIEW_ALREADY_EXISTS);
	}

	public static ReviewAlreadyExistsException withBookAndUser(UUID bookId, UUID userId) {
		ReviewAlreadyExistsException exception = new ReviewAlreadyExistsException();
		exception.addDetail("bookId", bookId);
		exception.addDetail("userId", userId);
		return exception;
	}
}