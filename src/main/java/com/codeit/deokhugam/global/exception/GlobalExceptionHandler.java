package com.codeit.deokhugam.global.exception;

import com.codeit.deokhugam.domain.notification.exception.NotificationException;
import java.time.Instant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  //  IllegalArgument와 MethodArgumentNotValid는 나중에 같이 추가.
  //  나중에 공통 상위 커스텀 예외를 만들고 그걸 상속하는 방향으로 수정 필요.
  @ExceptionHandler(NotificationException.class)
  public ResponseEntity<ErrorResponse> handleNotificationException(
      NotificationException exception) {
    ErrorResponse response = new ErrorResponse(
        Instant.now(),
        exception.getErrorCode().name(),
        exception.getMessage(),
        exception.getDetails(),
        exception.getClass().getSimpleName(),
        exception.getStatus()
    );

    return ResponseEntity.status(exception.getStatus()).body(response);
  }
}
