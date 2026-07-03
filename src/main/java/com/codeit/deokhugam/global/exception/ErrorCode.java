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
  COMMENT_ALREADY_DELETED(400, "이미 삭제된 댓글입니다.");

  private final int status;
  private final String message;
}
