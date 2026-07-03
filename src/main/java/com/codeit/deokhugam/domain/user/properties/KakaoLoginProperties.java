package com.codeit.deokhugam.domain.user.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "spring.security.oauth2.client.registration.kakao")
public class KakaoLoginProperties {

  private String clientId;
  private String clientSecret;
  private String redirectUri;

}
