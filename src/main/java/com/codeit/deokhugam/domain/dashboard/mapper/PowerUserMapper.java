package com.codeit.deokhugam.domain.dashboard.mapper;

import org.springframework.stereotype.Component;

import com.codeit.deokhugam.domain.dashboard.dto.response.PowerUserResponse;
import com.codeit.deokhugam.domain.dashboard.entity.PowerUser;

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
}
