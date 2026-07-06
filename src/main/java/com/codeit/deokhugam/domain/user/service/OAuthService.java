package com.codeit.deokhugam.domain.user.service;

import com.codeit.deokhugam.domain.user.dto.request.oauth.OauthKakaoLoginRequest;
import com.codeit.deokhugam.domain.user.dto.response.UserDto;

public interface OAuthService {

  UserDto kakaoLogin(OauthKakaoLoginRequest request);

}
