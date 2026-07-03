package com.codeit.deokhugam.domain.review.exception;

import java.util.HashMap;
import java.util.Map;

import com.codeit.deokhugam.global.exception.ErrorCode;

import lombok.Getter;

@Getter
public class ReviewException extends RuntimeException {

	private final ErrorCode errorCode;
	private final Map<String, Object> details;

	public ReviewException(ErrorCode errorCode) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
		this.details = new HashMap<>();
	}

	public void addDetail(String key, Object value) {
		this.getDetails().put(key, value);
	}

	public int getStatus() {
		return errorCode.getStatus();
	}
}