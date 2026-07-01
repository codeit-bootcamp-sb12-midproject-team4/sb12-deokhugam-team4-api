package com.codeit.deokhugam.domain.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserRegisterRequest(

    @NotBlank
    @Email
    String email,

    @NotBlank
    @Size(min = 2, max = 20)
    String nickname,

    @NotBlank
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,20}$",
        message = "비밀번호는 8~20자 영문, 숫자, 특수문자를 포함해야 합니다")
    String password

) {}