package com.codeit.deokhugam.domain.user.service.Impl;

import com.codeit.deokhugam.domain.user.OauthKakao;
import com.codeit.deokhugam.domain.user.dto.request.oauth.KakaoTokenResponse;
import com.codeit.deokhugam.domain.user.dto.request.oauth.KakaoUserResponse;
import com.codeit.deokhugam.domain.user.dto.request.oauth.OauthKakaoLoginRequest;
import com.codeit.deokhugam.domain.user.dto.response.UserDto;
import com.codeit.deokhugam.domain.user.properties.KakaoLoginProperties;
import com.codeit.deokhugam.domain.user.repository.OauthKakaoRepository;
import com.codeit.deokhugam.domain.user.repository.UserRepository;
import com.codeit.deokhugam.domain.user.service.OAuthService;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;
import org.springframework.http.HttpHeaders;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import com.codeit.deokhugam.domain.user.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class OAuthServiceImpl implements OAuthService {

  private final RestTemplate restTemplate = new RestTemplate();
  private final OauthKakaoRepository oauthKakaoRepository;
  private final KakaoLoginProperties kakaoLoginProperties;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;


  @Override
  @Transactional
  public UserDto kakaoLogin(OauthKakaoLoginRequest request) {

    // 토큰 받기
    String accessToken = getAccessToken(request.code());

    // 토근으로 사용자 정보 가져오기
    KakaoUserResponse kakaoUser = getKakaoUserInfo(accessToken);

    // 있어?->로그인, 없어? -> 회원가입
    OauthKakao oauthKakao = oauthKakaoRepository.findByKakaoId(kakaoUser.getId())
        .orElseGet(() -> signUp(kakaoUser));

    return UserDto.from(oauthKakao.getUser());

  }

  // 토큰 받기
  private String getAccessToken(@NotBlank String code) {

    String url = "https://kauth.kakao.com/oauth/token";

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("grant_type", "authorization_code");
    params.add("code", code);
    params.add("redirect_uri", kakaoLoginProperties.getRedirectUri());
    params.add("client_id", kakaoLoginProperties.getClientId());
    params.add("client_secret", kakaoLoginProperties.getClientSecret());

    HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);

    ResponseEntity<KakaoTokenResponse> response = restTemplate.exchange(
        url, HttpMethod.POST, requestEntity, KakaoTokenResponse.class
    );

    return response.getBody().getAccessToken();
  }

  // 사용자 정보 가져오기
  private KakaoUserResponse getKakaoUserInfo(String accessToken) {

    String url = "https://kapi.kakao.com/v2/user/me";
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(accessToken);
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

    HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

    ResponseEntity<KakaoUserResponse> response = restTemplate.exchange(
        url, HttpMethod.GET, requestEntity, KakaoUserResponse.class
    );

    return response.getBody();
  }

  private OauthKakao signUp(KakaoUserResponse kakaoUser){

    String email = kakaoUser.getKakaoAccount().getEmail();
    String nickname = kakaoUser.getKakaoAccount().getProfile().getNickname();

    String randomPassword = UUID.randomUUID().toString();

    User user = User.builder()
        .email(email)
        .nickname(nickname)
        .password(passwordEncoder.encode(randomPassword))
        .build();
    userRepository.save(user);

    // 회원가입
    OauthKakao oauthKakao = OauthKakao.builder()
        .user(user)
        .email(email)
        .kakaoId(kakaoUser.getId())
        .nickname(nickname)
        .build();
    return oauthKakaoRepository.save(oauthKakao);
  }


}
