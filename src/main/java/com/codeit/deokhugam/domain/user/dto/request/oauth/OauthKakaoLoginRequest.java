package com.codeit.deokhugam.domain.user.dto.request.oauth;

import jakarta.validation.constraints.NotBlank;

public record OauthKakaoLoginRequest(
    @NotBlank
    String code
) {}

