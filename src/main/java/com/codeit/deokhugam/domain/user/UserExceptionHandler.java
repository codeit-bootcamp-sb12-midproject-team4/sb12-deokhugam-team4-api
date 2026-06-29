package com.codeit.deokhugam.domain.user;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Map;
import java.util.NoSuchElementException;

@RestControllerAdvice(basePackages = "com.codeit.deokhugam.domain.user")
public class UserExceptionHandler {

  // 로그인 실패, 이메일 중복 등등등등등
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<?> handleIllegalArgument(IllegalArgumentException e) {
    return ResponseEntity
        .status(HttpStatus.UNAUTHORIZED)
        .body(Map.of(
            "code", "UNAUTHORIZED",
            "message", e.getMessage(),
            "timestamp", Instant.now()
        ));
  }

  // 사용자 없을 때
  @ExceptionHandler(NoSuchElementException.class)
  public ResponseEntity<?> handleNoSuchElement(NoSuchElementException e) {
    return ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .body(Map.of(
            "code", "USER_NOT_FOUND",
            "message", e.getMessage(),
            "timestamp", Instant.now()
        ));
  }
}