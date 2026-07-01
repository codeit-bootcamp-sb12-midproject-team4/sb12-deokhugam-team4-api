package com.codeit.deokhugam.domain.user.service;

import com.codeit.deokhugam.domain.user.dto.request.UserLoginRequest;
import com.codeit.deokhugam.domain.user.dto.request.UserRegisterRequest;
import com.codeit.deokhugam.domain.user.dto.request.UserUpdateRequest;
import com.codeit.deokhugam.domain.user.dto.response.UserDto;
import com.codeit.deokhugam.domain.user.repository.UserRepository;
import com.codeit.deokhugam.domain.user.User;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  // 회원가입
  @Override
  public UserDto create(UserRegisterRequest request) {
    if(userRepository.existsByEmail(request.email())){
      throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
    }

    String encodedPassword = passwordEncoder.encode(request.password());

    User user = new User(
        request.email(),
        request.nickname(),
        encodedPassword
    );
    userRepository.save(user);
    return UserDto.from(user);
  }

  // 로그인
  @Override
  public UserDto login(UserLoginRequest request) {
    User user = userRepository.findByEmail(request.email())
        .orElseThrow(() -> new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다."));

    if(!passwordEncoder.matches(request.password(), user.getPassword())){
      throw new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다.");
    }
    return UserDto.from(user);
  }

  // 사용자 정보 조회
  @Override
  public UserDto findById(UUID userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
    return UserDto.from(user);
  }

  // 사용자 논리 삭제
  @Override
  public void softDelete(UUID userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

    user.markDeleted();
    userRepository.save(user);
  }

  // 사용자 정보 수정
  @Override
  public UserDto update(UUID userId, UserUpdateRequest request) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
    user.updateNickname(request.nickname());
    return UserDto.from(user);
  }


}
