package com.codeit.deokhugam.domain.review.exception;

import java.util.UUID;

import com.codeit.deokhugam.global.exception.ErrorCode;

public class ReviewNotOwnedException extends ReviewException {

	public ReviewNotOwnedException() {
		super(ErrorCode.REVIEW_NOT_OWNED);
	}

	public static ReviewNotOwnedException withUserId(UUID userId) {
		ReviewNotOwnedException exception = new ReviewNotOwnedException();
		exception.addDetail("userId", userId);
		return exception;
	}
}
