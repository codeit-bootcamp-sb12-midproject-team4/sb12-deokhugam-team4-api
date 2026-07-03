package com.codeit.deokhugam.domain.comment.exception;

import com.codeit.deokhugam.global.exception.ErrorCode;

public class CommentNotFoundException extends CommentException {

    public CommentNotFoundException() {
        super(ErrorCode.COMMENT_NOT_FOUND);
    }
}