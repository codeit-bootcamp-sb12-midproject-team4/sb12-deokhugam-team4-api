package com.codeit.deokhugam.domain.dashboard.dto.response;

import java.util.UUID;

public record PowerUserResponse(
        UUID memberId,
        String nickname,
        String profileImageUrl,
        Integer ranking,
        Long activityCount
) {}
