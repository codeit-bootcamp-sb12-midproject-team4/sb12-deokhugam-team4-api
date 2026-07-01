package com.codeit.deokhugam.domain.user.dto.request.oauth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class KakaoTokenResponse {

  @JsonProperty("access_token")
  private String accessToken;
}
