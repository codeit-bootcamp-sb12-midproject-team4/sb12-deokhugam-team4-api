package com.codeit.deokhugam.domain.notification.exception;

import java.util.HashMap;
import java.util.Map;

import com.codeit.deokhugam.global.exception.ErrorCode;

import lombok.Getter;

@Getter
public class NotificationException extends RuntimeException {

	private final ErrorCode errorCode;
	private final Map<String, Object> details;

	public NotificationException(ErrorCode errorCode) {
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
