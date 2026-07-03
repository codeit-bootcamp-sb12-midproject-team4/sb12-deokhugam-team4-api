package com.codeit.deokhugam.domain.dashboard.exception;

import com.codeit.deokhugam.global.exception.ErrorCode;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class DashboardException extends RuntimeException {

	private final ErrorCode errorCode;
	private final Map<String, Object> details = new HashMap<>();

	public DashboardException(ErrorCode errorCode) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
	}

	public int getStatus() {
		return errorCode.getStatus();
	}
}