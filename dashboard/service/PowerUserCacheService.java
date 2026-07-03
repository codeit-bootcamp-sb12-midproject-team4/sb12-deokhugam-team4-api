package com.codeit.deokhugam.domain.dashboard.service;

import com.codeit.deokhugam.domain.dashboard.entity.PowerUser;
import com.codeit.deokhugam.domain.dashboard.repository.PowerUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PowerUserCacheService {

	private final PowerUserRepository repository;

	@Cacheable(
		cacheNames = "dashboardPowerUsers",
		key = "#datasetId"
	)
	public List<PowerUser> getPowerUsers(Long datasetId) {
		return repository.findByDatasetIdOrderByRankingAsc(datasetId);
	}
}