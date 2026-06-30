package com.codeit.deokhugam.domain.dashboard.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public record PowerUserResponse(
        Integer ranking,
        UUID userId,
        String nickname,
        BigDecimal score,
        Integer likeCount,
        Integer commentCount
) {}
