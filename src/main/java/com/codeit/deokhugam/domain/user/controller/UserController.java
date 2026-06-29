package com.codeit.deokhugam.domain.user.controller;

import com.codeit.deokhugam.domain.user.dto.request.UserLoginRequest;
import com.codeit.deokhugam.domain.user.dto.request.UserRegisterRequest;
import com.codeit.deokhugam.domain.user.dto.request.UserUpdateRequest;
import com.codeit.deokhugam.domain.user.dto.response.UserDto;
import com.codeit.deokhugam.domain.user.service.UserService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

  private final UserService userService;

  // 회원가입
  @PostMapping
  public ResponseEntity<UserDto> create(@RequestBody UserRegisterRequest request) {
    return ResponseEntity.ok(userService.create(request));
  }

  // 로그인
  @PostMapping("/login")
  public ResponseEntity<UserDto> login(@RequestBody UserLoginRequest request) {
      return ResponseEntity.ok(userService.login(request));
  }

  // 사용자 정보 조회
  @GetMapping("/{userId}")
  public ResponseEntity<UserDto> findById(@PathVariable UUID userId) {
      return ResponseEntity.ok(userService.findById(userId));
  }

  // 사용자 논리 삭제
  @DeleteMapping("/{userId}")
  public void delete(@PathVariable UUID userId) {
    userService.softDelete(userId);
  }

  // 사용자 정보 수정
  @PatchMapping("/{userId}")
  public ResponseEntity<UserDto> update(@PathVariable UUID userId, @RequestBody UserUpdateRequest request) {
    return ResponseEntity.ok(userService.update(userId, request));
  }
}