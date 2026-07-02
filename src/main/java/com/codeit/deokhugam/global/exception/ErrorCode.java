package com.codeit.deokhugam.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
	// Notification
	NOTIFICATION_NOT_OWNED(403, "본인의 알림만 조회/수정할 수 있습니다."),
	NOTIFICATION_NOT_FOUND(404, "알림을 찾을 수 없습니다."),

	// Review
	REVIEW_NOT_FOUND(404, "리뷰를 찾을 수 없습니다."),
	REVIEW_ALREADY_EXISTS(409, "해당 도서에 대한 리뷰가 이미 존재합니다."),
	REVIEW_NOT_OWNED(403, "해당 리뷰에 대한 권한이 없습니다.");

	private final int status;
	private final String message;
}
