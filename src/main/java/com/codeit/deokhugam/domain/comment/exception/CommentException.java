package com.codeit.deokhugam.domain.comment.exception;

import com.codeit.deokhugam.global.exception.DeokhugamException;
import com.codeit.deokhugam.global.exception.ErrorCode;

public class CommentException extends DeokhugamException {
	public CommentException(ErrorCode errorCode) {
		super(errorCode);
	}

	public CommentException(ErrorCode errorCode, Throwable cause) {
		super(errorCode, cause);
	}
}