package com.codeit.deokhugam.domain.dashboard.service;

import com.codeit.deokhugam.domain.dashboard.dto.response.FixedTopRankResponse;
import com.codeit.deokhugam.domain.dashboard.dto.response.PowerUserResponse;
import com.codeit.deokhugam.domain.dashboard.entity.PowerUser;
import com.codeit.deokhugam.domain.dashboard.mapper.PowerUserMapper;
import com.codeit.deokhugam.domain.dashboard.repository.PowerUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PowerUserService {

	private final PowerUserRepository repository;
	private final PowerUserMapper mapper;

	@Cacheable(
		cacheNames = "dashboardPowerUsers",
		key = "#datasetId"
	)
	public FixedTopRankResponse<PowerUserResponse> getPowerUsers(Long datasetId) {

		List<PowerUser> users = repository.findAllByDatasetId(datasetId);

		return mapper.toFixedTopRankResponse(users);
	}
}