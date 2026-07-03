package com.codeit.deokhugam.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

	// Notification
	NOTIFICATION_NOT_OWNED(403, "본인의 알림만 조회/수정할 수 있습니다."),
	NOTIFICATION_NOT_FOUND(404, "알림을 찾을 수 없습니다."),

	// Comment
	COMMENT_NOT_FOUND(404, "댓글을 찾을 수 없습니다."),
	COMMENT_NOT_OWNED(403, "본인의 댓글만 수정/삭제할 수 있습니다."),
	COMMENT_ALREADY_DELETED(400, "이미 삭제된 댓글입니다."),

	// Review
	REVIEW_NOT_FOUND(404, "리뷰를 찾을 수 없습니다."),
	REVIEW_ALREADY_EXISTS(409, "해당 도서에 대한 리뷰가 이미 존재합니다."),
	REVIEW_NOT_OWNED(403, "해당 리뷰에 대한 권한이 없습니다."),

	// Dashboard
	DATASET_NOT_FOUND(404, "대시보드 데이터셋을 찾을 수 없습니다."),
	INVALID_RANK(400, "rank 값이 올바르지 않습니다."),
	INVALID_LIMIT(400, "limit 값이 올바르지 않습니다."),
	INVALID_PERIOD(400, "period 값이 올바르지 않습니다."),
	INVALID_DATE_FORMAT(400, "날짜 형식이 올바르지 않습니다.");

	private final int status;
	private final String message;
}
