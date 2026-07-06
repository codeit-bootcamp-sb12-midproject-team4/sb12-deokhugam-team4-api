package com.codeit.deokhugam.global.exception;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;

@Getter
public class DeokhugamException extends RuntimeException {
	private final ErrorCode errorCode;
	private final Map<String, Object> details = new HashMap<>();

	public DeokhugamException(ErrorCode errorCode) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
	}

	public DeokhugamException(ErrorCode errorCode, Throwable cause) {
		super(errorCode.getMessage(), cause);
		this.errorCode = errorCode;
	}

	public DeokhugamException addDetail(String key, Object value) {
		this.getDetails().put(key, value);
		return this;
	}

	public int getStatus() {
		return errorCode.getStatus();
	}

}