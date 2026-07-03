package com.codeit.deokhugam.domain.dashboard.service;

import com.codeit.deokhugam.domain.dashboard.entity.PopularReview;
import com.codeit.deokhugam.domain.dashboard.repository.PopularReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PopularReviewCacheService {

	private final PopularReviewRepository repository;

	@Cacheable(
		cacheNames = "dashboardPopularReviews",
		key = "#datasetId"
	)
	public List<PopularReview> getPopularReviews(Long datasetId) {
		return repository.findByDatasetIdOrderByRankingAsc(datasetId);
	}
}
