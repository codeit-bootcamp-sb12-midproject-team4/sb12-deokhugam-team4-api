package com.codeit.deokhugam.domain.dashboard.mapper;

import com.codeit.deokhugam.domain.dashboard.dto.response.FixedTopRankResponse;
import com.codeit.deokhugam.domain.dashboard.dto.response.PowerUserResponse;
import com.codeit.deokhugam.domain.dashboard.entity.PowerUser;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PowerUserMapper {

    public PowerUserResponse toResponse(PowerUser entity) {

        return new PowerUserResponse(
            entity.getRanking(),
            entity.getUserId(),
            entity.getNickname(),
            entity.getScore(),
            entity.getLikeCount(),
            entity.getCommentCount()
        );
    }

    public FixedTopRankResponse<PowerUserResponse> toFixedTopRankResponse(
        List<PowerUser> entities) {

        List<PowerUserResponse> items = entities.stream()
            .map(this::toResponse)
            .toList();

        return new FixedTopRankResponse<>(items);
    }
}
