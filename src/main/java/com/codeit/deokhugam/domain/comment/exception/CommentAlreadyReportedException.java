package com.codeit.deokhugam.domain.comment.exception;

import com.codeit.deokhugam.global.exception.ErrorCode;

public class CommentAlreadyReportedException extends CommentException {

    public CommentAlreadyReportedException() {
        super(ErrorCode.COMMENT_ALREADY_REPORTED);
    }
}