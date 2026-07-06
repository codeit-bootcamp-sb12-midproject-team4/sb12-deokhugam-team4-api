package com.codeit.deokhugam.domain.comment.exception;

import com.codeit.deokhugam.global.exception.ErrorCode;

public class CommentNotOwnedException extends CommentException {

    public CommentNotOwnedException() {
        super(ErrorCode.COMMENT_NOT_OWNED);
    }
}