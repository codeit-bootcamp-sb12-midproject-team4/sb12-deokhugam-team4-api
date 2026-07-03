package com.codeit.deokhugam.domain.dashboard.service;

import com.codeit.deokhugam.domain.dashboard.entity.TrendingKeyword;
import com.codeit.deokhugam.domain.dashboard.repository.TrendingKeywordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TrendingKeywordCacheService {

	private final TrendingKeywordRepository repository;

	@Cacheable(
		cacheNames = "dashboardTrendingKeywords",
		key = "#datasetId"
	)
	public List<TrendingKeyword> getTrendingKeywords(Long datasetId) {

		return repository.findBySnapshot_DatasetIdOrderByRankingAsc(datasetId);
	}
}
