package com.codeit.deokhugam.domain.dashboard.service;

import com.codeit.deokhugam.domain.dashboard.dto.response.FixedTopRankResponse;
import com.codeit.deokhugam.domain.dashboard.dto.response.PowerUserResponse;
import com.codeit.deokhugam.domain.dashboard.entity.PowerUser;
import com.codeit.deokhugam.domain.dashboard.mapper.PowerUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PowerUserService {

	private final PowerUserCacheService cacheService;
	private final PowerUserMapper mapper;

	public FixedTopRankResponse<PowerUserResponse> getPowerUsers(Long datasetId) {

		List<PowerUser> users = cacheService.getPowerUsers(datasetId);

		return mapper.toFixedTopRankResponse(users);
	}
}