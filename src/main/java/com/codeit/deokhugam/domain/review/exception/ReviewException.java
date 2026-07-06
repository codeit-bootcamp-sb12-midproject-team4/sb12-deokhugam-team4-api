package com.codeit.deokhugam.domain.review.exception;

import com.codeit.deokhugam.global.exception.DeokhugamException;
import com.codeit.deokhugam.global.exception.ErrorCode;

import lombok.Getter;

@Getter
public class ReviewException extends DeokhugamException {
	public ReviewException(ErrorCode errorCode) {
		super(errorCode);
	}

	public ReviewException(ErrorCode errorCode, Throwable cause) {
		super(errorCode, cause);
	}
}