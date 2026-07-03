package com.codeit.deokhugam.domain.comment.exception;

import com.codeit.deokhugam.global.exception.ErrorCode;
import lombok.Getter;
import java.util.HashMap;
import java.util.Map;

@Getter
public class CommentException extends RuntimeException {

    private final ErrorCode errorCode;
    private final Map<String, Object> details;

    public CommentException(ErrorCode errorCode) {
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