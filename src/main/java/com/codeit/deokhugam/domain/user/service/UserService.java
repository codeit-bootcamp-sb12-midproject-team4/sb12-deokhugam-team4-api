package com.codeit.deokhugam.domain.user.service;

import com.codeit.deokhugam.domain.user.User;
import com.codeit.deokhugam.domain.user.dto.request.UserLoginRequest;
import com.codeit.deokhugam.domain.user.dto.request.UserRegisterRequest;
import com.codeit.deokhugam.domain.user.dto.request.UserUpdateRequest;
import com.codeit.deokhugam.domain.user.dto.response.UserDto;
import java.util.UUID;

public interface UserService {
  UserDto create(UserRegisterRequest request);            // 회원가입
  UserDto login(UserLoginRequest request);                // 로그인
  UserDto findById(UUID userId);                          // 사용자 정보 조회
  void softDelete(UUID userId);                           // 사용자 논리 삭제
  UserDto update(UUID userId, UserUpdateRequest request); // 사용자 닉네임 수정
}
