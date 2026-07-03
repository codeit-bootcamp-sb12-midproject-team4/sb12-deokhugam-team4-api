package com.codeit.deokhugam.domain.review.exception;

import java.util.UUID;

import com.codeit.deokhugam.global.exception.ErrorCode;

public class ReviewNotFoundException extends ReviewException {

	public ReviewNotFoundException() {
		super(ErrorCode.REVIEW_NOT_FOUND);
	}

	public static ReviewNotFoundException withReviewId(UUID reviewId) {
		ReviewNotFoundException exception = new ReviewNotFoundException();
		exception.addDetail("reviewId", reviewId);
		return exception;
	}
}