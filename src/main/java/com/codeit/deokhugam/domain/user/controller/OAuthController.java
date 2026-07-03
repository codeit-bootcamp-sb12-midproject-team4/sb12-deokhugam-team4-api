package com.codeit.deokhugam.domain.user.controller;

import com.codeit.deokhugam.domain.user.dto.request.oauth.OauthKakaoLoginRequest;
import com.codeit.deokhugam.domain.user.dto.response.UserDto;
import com.codeit.deokhugam.domain.user.service.OAuthService;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.http.HttpHeaders;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class OAuthController {

  private final OAuthService oAuthService;

  @GetMapping("/auth/kakao/callback")
  public ResponseEntity<Void> kakaoCallback(@RequestParam String code) {
    OauthKakaoLoginRequest request = new OauthKakaoLoginRequest(code);
    UserDto userDto = oAuthService.kakaoLogin(request);

    HttpHeaders headers = new HttpHeaders();
    headers.setLocation(URI.create("/#/kakao/callback?userId=" + userDto.id()));
    return new ResponseEntity<>(headers, HttpStatus.FOUND);
  }

}